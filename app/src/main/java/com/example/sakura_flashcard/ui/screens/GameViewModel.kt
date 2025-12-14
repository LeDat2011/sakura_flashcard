package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.LeaderboardEntry
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.ui.games.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow<GameUiState>(
        GameUiState.GameSelection(GameType.values().toList())
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    private val _gameStatistics = MutableStateFlow<GameStatistics?>(null)
    val gameStatistics: StateFlow<GameStatistics?> = _gameStatistics.asStateFlow()
    
    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()
    
    private var currentGame: MiniGame? = null
    private var timerJob: Job? = null
    private var flippedCards = mutableListOf<String>()
    private var memoryCards = mutableListOf<MemoryCard>()
    private var currentUserId: String = "current_user"
    private var currentLevel: JLPTLevel = JLPTLevel.N5
    private var gameStartTime: Long = 0L
    
    fun startGame(gameType: GameType, level: JLPTLevel) {
        viewModelScope.launch {
            try {
                _uiState.value = GameUiState.Loading
                currentLevel = level
                gameStartTime = System.currentTimeMillis()
                
                currentGame = when (gameType) {
                    GameType.SENTENCE_ORDER_PUZZLE -> MiniGameFactory.createSentenceOrderPuzzle(level)
                    GameType.QUICK_ANSWER_CHALLENGE -> MiniGameFactory.createQuickAnswerChallenge(level)
                    GameType.MEMORY_MATCH_GAME -> MiniGameFactory.createMemoryMatchGame(level)
                }
                
                val gameState = currentGame?.startGame()
                
                if (gameState is GameState.InProgress) {
                    _uiState.value = GameUiState.GameInProgress(gameType, gameState)
                    
                    if (gameType == GameType.QUICK_ANSWER_CHALLENGE) {
                        startQuestionTimer(gameState)
                    }
                    
                    if (gameType == GameType.MEMORY_MATCH_GAME) {
                        initializeMemoryCards(gameState)
                    }
                } else {
                    _uiState.value = GameUiState.Error("Failed to start game")
                }
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error("Error starting game: ${e.message}")
            }
        }
    }
    
    fun processGameInput(input: GameInput) {
        viewModelScope.launch {
            try {
                val game = currentGame ?: return@launch
                val currentState = _uiState.value as? GameUiState.GameInProgress ?: return@launch
                
                val newState = game.processInput(input)
                
                when (newState) {
                    is GameState.InProgress -> {
                        _uiState.value = currentState.copy(gameState = newState)
                        
                        if (currentState.gameType == GameType.QUICK_ANSWER_CHALLENGE) {
                            startQuestionTimer(newState)
                        }
                    }
                    is GameState.Completed -> {
                        timerJob?.cancel()
                        val gameResult = GameResult(
                            userId = currentUserId,
                            gameType = game.type,
                            score = newState.finalScore,
                            timeSpentSeconds = newState.totalTime,
                            level = currentLevel
                        )
                        
                        _uiState.value = GameUiState.GameCompleted(gameResult, newState)
                        updateGameStatistics(gameResult, newState)
                    }
                    else -> {
                        _uiState.value = GameUiState.Error("Invalid game state")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error("Error processing input: ${e.message}")
            }
        }
    }
    
    fun handleCardFlip(cardId: String) {
        val currentState = _uiState.value as? GameUiState.GameInProgress ?: return
        
        val clickedCard = memoryCards.find { it.id == cardId }
        if (clickedCard == null || clickedCard.isFlipped || clickedCard.isMatched) return
        
        if (flippedCards.size >= 2) return
        
        memoryCards = memoryCards.map { card ->
            if (card.id == cardId) card.copy(isFlipped = true) else card
        }.toMutableList()
        
        flippedCards.add(cardId)
        
        val updatedGameState = currentState.gameState.copy(
            currentQuestion = memoryCards.toList()
        )
        _uiState.value = currentState.copy(gameState = updatedGameState)
        
        if (flippedCards.size == 2) {
            viewModelScope.launch {
                delay(800)
                
                val card1 = memoryCards.find { it.id == flippedCards[0] }
                val card2 = memoryCards.find { it.id == flippedCards[1] }
                
                if (card1 != null && card2 != null && card1.matchId == card2.matchId) {
                    memoryCards = memoryCards.map { card ->
                        if (card.id == card1.id || card.id == card2.id) {
                            card.copy(isMatched = true)
                        } else card
                    }.toMutableList()
                    
                    processGameInput(GameInput.CardMatch(card1.content, card2.content))
                } else {
                    memoryCards = memoryCards.map { card ->
                        if (card.id == flippedCards[0] || card.id == flippedCards[1]) {
                            card.copy(isFlipped = false)
                        } else card
                    }.toMutableList()
                    
                    val newState = _uiState.value as? GameUiState.GameInProgress
                    if (newState != null) {
                        val newGameState = newState.gameState.copy(
                            currentQuestion = memoryCards.toList()
                        )
                        _uiState.value = newState.copy(gameState = newGameState)
                    }
                }
                
                flippedCards.clear()
            }
        }
    }
    
    fun backToGameSelection() {
        timerJob?.cancel()
        currentGame = null
        flippedCards.clear()
        memoryCards.clear()
        _uiState.value = GameUiState.GameSelection(GameType.values().toList())
    }
    
    private fun updateGameStatistics(gameResult: GameResult, gameState: GameState.Completed) {
        val currentStats = _gameStatistics.value ?: GameStatistics()
        
        val updatedStats = currentStats.copy(
            totalGamesPlayed = currentStats.totalGamesPlayed + 1,
            totalScore = currentStats.totalScore + gameResult.score,
            totalTimeSpent = currentStats.totalTimeSpent + gameResult.timeSpentSeconds,
            averageAccuracy = calculateNewAverage(
                currentStats.averageAccuracy,
                gameState.performance.accuracy,
                currentStats.totalGamesPlayed
            ),
            bestScores = updateBestScores(currentStats.bestScores, gameResult),
            gameTypeStats = updateGameTypeStats(currentStats.gameTypeStats, gameResult, gameState)
        )
        
        _gameStatistics.value = updatedStats
    }
    
    private fun calculateNewAverage(currentAverage: Float, newValue: Float, count: Int): Float {
        return if (count == 0) newValue else (currentAverage * count + newValue) / (count + 1)
    }
    
    private fun updateBestScores(currentBestScores: Map<GameType, Int>, gameResult: GameResult): Map<GameType, Int> {
        val currentBest = currentBestScores[gameResult.gameType] ?: 0
        return currentBestScores.toMutableMap().apply {
            put(gameResult.gameType, maxOf(currentBest, gameResult.score))
        }
    }
    
    private fun updateGameTypeStats(
        currentStats: Map<GameType, GameTypeStatistics>,
        gameResult: GameResult,
        gameState: GameState.Completed
    ): Map<GameType, GameTypeStatistics> {
        val currentTypeStat = currentStats[gameResult.gameType] ?: GameTypeStatistics()
        
        val updatedTypeStat = currentTypeStat.copy(
            gamesPlayed = currentTypeStat.gamesPlayed + 1,
            averageScore = calculateNewAverage(
                currentTypeStat.averageScore,
                gameResult.score.toFloat(),
                currentTypeStat.gamesPlayed
            ),
            bestScore = maxOf(currentTypeStat.bestScore, gameResult.score),
            averageTime = calculateNewAverage(
                currentTypeStat.averageTime,
                gameResult.timeSpentSeconds.toFloat(),
                currentTypeStat.gamesPlayed
            )
        )
        
        return currentStats.toMutableMap().apply {
            put(gameResult.gameType, updatedTypeStat)
        }
    }
    
    private fun startQuestionTimer(gameState: GameState.InProgress) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var timeRemaining = gameState.timeRemaining ?: return@launch
            
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
                
                val currentState = _uiState.value as? GameUiState.GameInProgress
                if (currentState != null) {
                    val updatedGameState = currentState.gameState.copy(timeRemaining = timeRemaining)
                    _uiState.value = currentState.copy(gameState = updatedGameState)
                }
            }
        }
    }
    
    private fun initializeMemoryCards(gameState: GameState.InProgress) {
        val cards = gameState.currentQuestion as? List<*>
        if (cards != null) {
            @Suppress("UNCHECKED_CAST")
            memoryCards = (cards as List<MemoryCard>).toMutableList()
            flippedCards.clear()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

sealed class GameUiState {
    data class GameSelection(val availableGames: List<GameType>) : GameUiState()
    data class GameInProgress(val gameType: GameType, val gameState: GameState.InProgress) : GameUiState()
    data class GameCompleted(val gameResult: GameResult, val completedState: GameState.Completed) : GameUiState()
    object Loading : GameUiState()
    data class Error(val message: String) : GameUiState()
}