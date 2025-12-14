package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.VocabularyData
import com.example.sakura_flashcard.data.algorithm.LearningRecommendations
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.toFlashcard
import com.example.sakura_flashcard.data.api.toUser
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.repository.LearningStatistics
import com.example.sakura_flashcard.data.repository.SpacedRepetitionRepository
import com.example.sakura_flashcard.ui.components.InteractionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val recommendedFlashcards: List<Flashcard> = emptyList(),
    val learningRecommendations: LearningRecommendations? = null,
    val learningStatistics: LearningStatistics? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val spacedRepetitionRepository: SpacedRepetitionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null
    
    fun loadUserData(userId: String) {
        if (currentUserId == userId && _uiState.value.user != null) {
            // Data already loaded for this user
            return
        }
        
        currentUserId = userId
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                // Load user data
                val userResponse = apiService.getProfile()
                if (!userResponse.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load user data: ${userResponse.message()}"
                    )
                    return@launch
                }
                
                val user = userResponse.body()?.data?.toUser()
                
                // Load recommended flashcards using spaced repetition algorithm
                val flashcardsResult = spacedRepetitionRepository.getRecommendedFlashcards(userId, 20)
                var recommendedFlashcards = flashcardsResult.getOrElse { emptyList() }
                
                // Nếu không có thẻ gợi ý, lấy thẻ ngẫu nhiên từ vocabularies
                if (recommendedFlashcards.isEmpty()) {
                    try {
                        val vocabResponse = apiService.getRecommendedFlashcards(limit = 10)
                        if (vocabResponse.isSuccessful) {
                            val randomFlashcards = vocabResponse.body()?.data?.map { it.toFlashcard() }?.shuffled()?.take(5)
                            if (!randomFlashcards.isNullOrEmpty()) {
                                recommendedFlashcards = randomFlashcards
                            }
                        }
                    } catch (e: Exception) {
                        // Nếu lỗi API, dùng data local
                        recommendedFlashcards = VocabularyData.getAllN5Flashcards().shuffled().take(5)
                    }
                }
                
                // Load learning recommendations
                val recommendationsResult = spacedRepetitionRepository.getLearningRecommendations(userId)
                val learningRecommendations = recommendationsResult.getOrNull()
                
                // Load learning statistics
                val statisticsResult = spacedRepetitionRepository.getLearningStatistics(userId)
                val learningStatistics = statisticsResult.getOrNull()
                
                _uiState.value = _uiState.value.copy(
                    user = user,
                    recommendedFlashcards = recommendedFlashcards,
                    learningRecommendations = learningRecommendations,
                    learningStatistics = learningStatistics,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            }
        }
    }
    
    fun handleFlashcardInteraction(flashcard: Flashcard, interactionType: InteractionType) {
        val userId = currentUserId ?: return
        
        when (interactionType) {
            InteractionType.LEARNED -> {
                updateFlashcardProgress(flashcard, userId, ReviewQuality.CORRECT_EASY)
            }
            InteractionType.NOT_LEARNED -> {
                updateFlashcardProgress(flashcard, userId, ReviewQuality.INCORRECT_HARD)
            }
            InteractionType.PLAY_AUDIO -> {
                // Handle audio playback - this would typically involve a media player
                // For now, we'll just log the interaction
                println("Playing audio for flashcard: ${flashcard.id}")
            }
            InteractionType.FLIP -> {
                // Flip is handled by the UI component itself
                // No backend interaction needed
            }
            InteractionType.SKIP -> {
                // Skip - do nothing
            }
        }
    }
    
    /**
     * Handles detailed flashcard review with quality rating
     */
    fun handleFlashcardReview(
        flashcard: Flashcard, 
        quality: ReviewQuality, 
        responseTime: Long = 0L
    ) {
        val userId = currentUserId ?: return
        updateFlashcardProgress(flashcard, userId, quality, responseTime)
    }
    
    private fun updateFlashcardProgress(
        flashcard: Flashcard, 
        userId: String, 
        quality: ReviewQuality, 
        responseTime: Long = 0L
    ) {
        viewModelScope.launch {
            try {
                val result = spacedRepetitionRepository.updateFlashcardProgress(
                    userId = userId,
                    flashcardId = flashcard.id,
                    quality = quality,
                    responseTime = responseTime
                )
                
                result.fold(
                    onSuccess = { spacedRepetitionData ->
                        // Update local state
                        updateLocalFlashcardState(flashcard.id, quality.isCorrect())
                        
                        // Refresh recommendations if this was a significant learning event
                        if (quality.isCorrect()) {
                            refreshLearningData(userId)
                        }
                    },
                    onFailure = { error ->
                        println("Failed to update flashcard progress: ${error.message}")
                        // Could show error to user here
                    }
                )
            } catch (e: Exception) {
                println("Error updating flashcard progress: ${e.message}")
            }
        }
    }
    
    private fun updateLocalFlashcardState(flashcardId: String, isLearned: Boolean) {
        val currentFlashcards = _uiState.value.recommendedFlashcards
        val updatedFlashcards = currentFlashcards.map { flashcard ->
            if (flashcard.id == flashcardId) {
                flashcard.markAsReviewed()
            } else {
                flashcard
            }
        }
        
        _uiState.value = _uiState.value.copy(recommendedFlashcards = updatedFlashcards)
    }
    
    /**
     * Refreshes learning data including recommendations and statistics
     */
    private fun refreshLearningData(userId: String) {
        viewModelScope.launch {
            try {
                // Refresh learning recommendations
                val recommendationsResult = spacedRepetitionRepository.getLearningRecommendations(userId)
                val learningRecommendations = recommendationsResult.getOrNull()
                
                // Refresh learning statistics
                val statisticsResult = spacedRepetitionRepository.getLearningStatistics(userId)
                val learningStatistics = statisticsResult.getOrNull()
                
                _uiState.value = _uiState.value.copy(
                    learningRecommendations = learningRecommendations,
                    learningStatistics = learningStatistics
                )
            } catch (e: Exception) {
                println("Error refreshing learning data: ${e.message}")
            }
        }
    }
    
    fun refreshData() {
        currentUserId?.let { userId ->
            loadUserData(userId)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Gets due cards for focused review session
     */
    fun getDueCards() {
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            try {
                val result = spacedRepetitionRepository.getDueCards(userId)
                result.fold(
                    onSuccess = { dueCards ->
                        _uiState.value = _uiState.value.copy(
                            recommendedFlashcards = dueCards
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load due cards: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error loading due cards: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Starts a focused study session with optimal number of cards
     */
    fun startStudySession() {
        val userId = currentUserId ?: return
        val recommendations = _uiState.value.learningRecommendations
        
        if (recommendations != null) {
            viewModelScope.launch {
                try {
                    val sessionSize = recommendations.optimalSessionSize
                    val result = spacedRepetitionRepository.getRecommendedFlashcards(userId, sessionSize)
                    
                    result.fold(
                        onSuccess = { sessionCards ->
                            _uiState.value = _uiState.value.copy(
                                recommendedFlashcards = sessionCards
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = _uiState.value.copy(
                                error = "Failed to start study session: ${error.message}"
                            )
                        }
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        error = "Error starting study session: ${e.message}"
                    )
                }
            }
        }
    }
}