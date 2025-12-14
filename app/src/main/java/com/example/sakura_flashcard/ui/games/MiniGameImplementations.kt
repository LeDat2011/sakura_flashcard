package com.example.sakura_flashcard.ui.games

import com.example.sakura_flashcard.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

// Data class for memory game cards
data class MemoryCard(
    val id: String,
    val content: String,
    val matchId: String, // Cards with same matchId are pairs
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

class SentenceOrderPuzzle(
    private val sentences: List<SentencePuzzle>
) : MiniGame {
    
    override val name: String = "Sentence Order Puzzle"
    override val description: String = "Drag words to form correct Japanese sentences"
    override val type: GameType = GameType.SENTENCE_ORDER_PUZZLE
    
    private var currentSentenceIndex = 0
    private var score = 0
    private var questionsCompleted = 0
    private val totalQuestions = minOf(sentences.size, 10)
    
    override fun startGame(): GameState {
        currentSentenceIndex = 0
        score = 0
        questionsCompleted = 0
        
        return if (sentences.isNotEmpty()) {
            GameState.InProgress(
                currentScore = score,
                timeRemaining = null, // No time limit for sentence puzzle
                currentQuestion = sentences[currentSentenceIndex],
                questionsCompleted = questionsCompleted,
                totalQuestions = totalQuestions
            )
        } else {
            GameState.NotStarted
        }
    }
    
    override fun processInput(input: GameInput): GameState {
        return when (input) {
            is GameInput.WordOrder -> {
                val currentSentence = sentences[currentSentenceIndex]
                val isCorrect = input.orderedWords == currentSentence.correctOrder
                
                if (isCorrect) {
                    score += 10
                }
                
                questionsCompleted++
                currentSentenceIndex++
                
                if (currentSentenceIndex >= totalQuestions || currentSentenceIndex >= sentences.size) {
                    // Game completed
                    val accuracy = score.toFloat() / (totalQuestions * 10)
                    val performance = GamePerformance(
                        accuracy = accuracy,
                        speed = questionsCompleted.toFloat() / 60f, // Assume 1 minute per question average
                        improvement = 0f // Would need previous results to calculate
                    )
                    
                    GameState.Completed(
                        finalScore = score,
                        totalTime = 60L * questionsCompleted, // Estimated time
                        performance = performance,
                        questionsCompleted = questionsCompleted,
                        totalQuestions = totalQuestions
                    )
                } else {
                    // Continue to next question
                    GameState.InProgress(
                        currentScore = score,
                        timeRemaining = null,
                        currentQuestion = sentences[currentSentenceIndex],
                        questionsCompleted = questionsCompleted,
                        totalQuestions = totalQuestions
                    )
                }
            }
            else -> throw IllegalArgumentException("Invalid input type for Sentence Order Puzzle")
        }
    }
    
    override fun getMaxScore(): Int = totalQuestions * 10
    override fun getTimeLimit(): Long? = null // No time limit
}

class QuickAnswerChallenge(
    private val questions: List<QuickAnswerQuestion>
) : MiniGame {
    
    override val name: String = "Quick Answer Challenge"
    override val description: String = "Answer questions as fast as possible with 5-second timer"
    override val type: GameType = GameType.QUICK_ANSWER_CHALLENGE
    
    private var currentQuestionIndex = 0
    private var score = 0
    private var questionsCompleted = 0
    private val totalQuestions = minOf(questions.size, 10)
    private var startTime = 0L
    
    override fun startGame(): GameState {
        currentQuestionIndex = 0
        score = 0
        questionsCompleted = 0
        startTime = System.currentTimeMillis()
        
        return if (questions.isNotEmpty()) {
            GameState.InProgress(
                currentScore = score,
                timeRemaining = 5L, // 5-second timer per question
                currentQuestion = questions[currentQuestionIndex],
                questionsCompleted = questionsCompleted,
                totalQuestions = totalQuestions
            )
        } else {
            GameState.NotStarted
        }
    }
    
    override fun processInput(input: GameInput): GameState {
        return when (input) {
            is GameInput.Answer -> {
                val currentQuestion = questions[currentQuestionIndex]
                val isCorrect = input.answer == currentQuestion.correctAnswer
                
                if (isCorrect) {
                    score += 10
                }
                
                questionsCompleted++
                currentQuestionIndex++
                
                if (currentQuestionIndex >= totalQuestions || currentQuestionIndex >= questions.size) {
                    // Game completed
                    val totalTime = (System.currentTimeMillis() - startTime) / 1000L
                    val accuracy = score.toFloat() / (totalQuestions * 10)
                    val speed = questionsCompleted.toFloat() / totalTime
                    
                    val performance = GamePerformance(
                        accuracy = accuracy,
                        speed = speed,
                        improvement = 0f // Would need previous results to calculate
                    )
                    
                    GameState.Completed(
                        finalScore = score,
                        totalTime = totalTime,
                        performance = performance,
                        questionsCompleted = questionsCompleted,
                        totalQuestions = totalQuestions
                    )
                } else {
                    // Continue to next question
                    GameState.InProgress(
                        currentScore = score,
                        timeRemaining = 5L, // Reset timer for next question
                        currentQuestion = questions[currentQuestionIndex],
                        questionsCompleted = questionsCompleted,
                        totalQuestions = totalQuestions
                    )
                }
            }
            else -> throw IllegalArgumentException("Invalid input type for Quick Answer Challenge")
        }
    }
    
    override fun getMaxScore(): Int = totalQuestions * 10
    override fun getTimeLimit(): Long = 5L // 5 seconds per question
}

class MemoryMatchGame(
    private val wordPairs: List<WordPair>
) : MiniGame {
    
    override val name: String = "Memory Match Game"
    override val description: String = "Match Japanese words with their meanings by flipping cards"
    override val type: GameType = GameType.MEMORY_MATCH_GAME
    
    private var score = 0
    private var matchesFound = 0
    private var attempts = 0
    private val totalMatches = minOf(wordPairs.size, 8) // 8 pairs = 16 cards
    private var startTime = 0L
    
    override fun startGame(): GameState {
        score = 0
        matchesFound = 0
        attempts = 0
        startTime = System.currentTimeMillis()
        
        return if (wordPairs.isNotEmpty()) {
            val gameCards = createGameCards()
            GameState.InProgress(
                currentScore = score,
                timeRemaining = null, // No time limit for memory game
                currentQuestion = gameCards,
                questionsCompleted = matchesFound,
                totalQuestions = totalMatches
            )
        } else {
            GameState.NotStarted
        }
    }
    
    override fun processInput(input: GameInput): GameState {
        return when (input) {
            is GameInput.CardMatch -> {
                attempts++
                
                // Check if the cards match
                val pair = wordPairs.find { 
                    (it.japanese == input.card1 && it.english == input.card2) ||
                    (it.english == input.card1 && it.japanese == input.card2)
                }
                
                if (pair != null) {
                    // Match found
                    matchesFound++
                    score += 20 // Higher score for matches
                    
                    if (matchesFound >= totalMatches) {
                        // Game completed
                        val totalTime = (System.currentTimeMillis() - startTime) / 1000L
                        val accuracy = matchesFound.toFloat() / attempts
                        val speed = matchesFound.toFloat() / totalTime
                        
                        val performance = GamePerformance(
                            accuracy = accuracy,
                            speed = speed,
                            improvement = 0f // Would need previous results to calculate
                        )
                        
                        GameState.Completed(
                            finalScore = score,
                            totalTime = totalTime,
                            performance = performance,
                            questionsCompleted = matchesFound,
                            totalQuestions = totalMatches
                        )
                    } else {
                        // Continue game
                        GameState.InProgress(
                            currentScore = score,
                            timeRemaining = null,
                            currentQuestion = createGameCards(), // Updated cards state
                            questionsCompleted = matchesFound,
                            totalQuestions = totalMatches
                        )
                    }
                } else {
                    // No match, continue game
                    GameState.InProgress(
                        currentScore = score,
                        timeRemaining = null,
                        currentQuestion = createGameCards(),
                        questionsCompleted = matchesFound,
                        totalQuestions = totalMatches
                    )
                }
            }
            else -> throw IllegalArgumentException("Invalid input type for Memory Match Game")
        }
    }
    
    private fun createGameCards(): List<MemoryCard> {
        val cards = mutableListOf<MemoryCard>()
        
        wordPairs.take(totalMatches).forEachIndexed { index, pair ->
            val matchId = "match_$index"
            cards.add(MemoryCard(
                id = "japanese_$index",
                content = pair.japanese,
                matchId = matchId
            ))
            cards.add(MemoryCard(
                id = "english_$index", 
                content = pair.english,
                matchId = matchId
            ))
        }
        
        return cards.shuffled()
    }
    
    override fun getMaxScore(): Int = totalMatches * 20
    override fun getTimeLimit(): Long? = null // No time limit
}

// Data classes for game content
data class SentencePuzzle(
    val id: String,
    val words: List<String>,
    val correctOrder: List<String>,
    val translation: String,
    val level: JLPTLevel
)

data class QuickAnswerQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val level: JLPTLevel
)

data class WordPair(
    val id: String,
    val japanese: String,
    val english: String,
    val level: JLPTLevel
)

// Game factory for creating mini-games
object MiniGameFactory {
    
    fun createSentenceOrderPuzzle(level: JLPTLevel): SentenceOrderPuzzle {
        val sentences = getSampleSentences(level)
        return SentenceOrderPuzzle(sentences)
    }
    
    fun createQuickAnswerChallenge(level: JLPTLevel): QuickAnswerChallenge {
        val questions = getSampleQuestions(level)
        return QuickAnswerChallenge(questions)
    }
    
    fun createMemoryMatchGame(level: JLPTLevel): MemoryMatchGame {
        val wordPairs = getSampleWordPairs(level)
        return MemoryMatchGame(wordPairs)
    }
    
    private fun getSampleSentences(level: JLPTLevel): List<SentencePuzzle> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                SentencePuzzle("1", listOf("私", "は", "学生", "です"), listOf("私", "は", "学生", "です"), "I am a student", level),
                SentencePuzzle("2", listOf("これ", "は", "本", "です"), listOf("これ", "は", "本", "です"), "This is a book", level),
                SentencePuzzle("3", listOf("あれ", "は", "何", "ですか"), listOf("あれ", "は", "何", "ですか"), "What is that?", level),
                SentencePuzzle("4", listOf("日本語", "を", "勉強", "します"), listOf("日本語", "を", "勉強", "します"), "I study Japanese", level),
                SentencePuzzle("5", listOf("毎日", "学校", "に", "行きます"), listOf("毎日", "学校", "に", "行きます"), "I go to school every day", level),
                SentencePuzzle("6", listOf("お茶", "を", "飲みます"), listOf("お茶", "を", "飲みます"), "I drink tea", level),
                SentencePuzzle("7", listOf("友達", "と", "話します"), listOf("友達", "と", "話します"), "I talk with friends", level),
                SentencePuzzle("8", listOf("朝", "ご飯", "を", "食べます"), listOf("朝", "ご飯", "を", "食べます"), "I eat breakfast", level),
                SentencePuzzle("9", listOf("電車", "で", "会社", "に", "行きます"), listOf("電車", "で", "会社", "に", "行きます"), "I go to work by train", level),
                SentencePuzzle("10", listOf("今", "何時", "ですか"), listOf("今", "何時", "ですか"), "What time is it now?", level)
            )
            else -> listOf(
                SentencePuzzle("1", listOf("今日", "は", "天気", "が", "いい", "です"), listOf("今日", "は", "天気", "が", "いい", "です"), "The weather is nice today", level),
                SentencePuzzle("2", listOf("明日", "映画", "を", "見", "に", "行きます"), listOf("明日", "映画", "を", "見", "に", "行きます"), "I will go to see a movie tomorrow", level),
                SentencePuzzle("3", listOf("この", "料理", "は", "とても", "おいしい", "です"), listOf("この", "料理", "は", "とても", "おいしい", "です"), "This dish is very delicious", level)
            )
        }
    }
    
    private fun getSampleQuestions(level: JLPTLevel): List<QuickAnswerQuestion> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                QuickAnswerQuestion("1", "What does 'こんにちは' mean?", listOf("Hello", "Goodbye", "Thank you", "Excuse me"), "Hello", level),
                QuickAnswerQuestion("2", "What does '学生' mean?", listOf("Teacher", "Student", "School", "Book"), "Student", level),
                QuickAnswerQuestion("3", "What does 'ありがとう' mean?", listOf("Sorry", "Hello", "Thank you", "Goodbye"), "Thank you", level),
                QuickAnswerQuestion("4", "What does '食べる' mean?", listOf("To drink", "To eat", "To sleep", "To walk"), "To eat", level),
                QuickAnswerQuestion("5", "What does '水' mean?", listOf("Fire", "Water", "Earth", "Wind"), "Water", level),
                QuickAnswerQuestion("6", "What does '犬' mean?", listOf("Cat", "Bird", "Dog", "Fish"), "Dog", level),
                QuickAnswerQuestion("7", "What does '大きい' mean?", listOf("Small", "Big", "Fast", "Slow"), "Big", level),
                QuickAnswerQuestion("8", "What does '赤' mean?", listOf("Blue", "Green", "Red", "Yellow"), "Red", level),
                QuickAnswerQuestion("9", "What does '今日' mean?", listOf("Yesterday", "Tomorrow", "Today", "Next week"), "Today", level),
                QuickAnswerQuestion("10", "What does '友達' mean?", listOf("Family", "Friend", "Teacher", "Student"), "Friend", level)
            )
            else -> listOf(
                QuickAnswerQuestion("1", "What does '天気' mean?", listOf("Weather", "Time", "Place", "Person"), "Weather", level),
                QuickAnswerQuestion("2", "What does '仕事' mean?", listOf("Study", "Work", "Play", "Rest"), "Work", level),
                QuickAnswerQuestion("3", "What does '旅行' mean?", listOf("Travel", "Shopping", "Cooking", "Reading"), "Travel", level)
            )
        }
    }
    
    private fun getSampleWordPairs(level: JLPTLevel): List<WordPair> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                WordPair("1", "猫", "Cat", level),
                WordPair("2", "犬", "Dog", level),
                WordPair("3", "本", "Book", level),
                WordPair("4", "水", "Water", level),
                WordPair("5", "食べ物", "Food", level),
                WordPair("6", "家", "House", level),
                WordPair("7", "車", "Car", level),
                WordPair("8", "学校", "School", level)
            )
            else -> listOf(
                WordPair("1", "天気", "Weather", level),
                WordPair("2", "時間", "Time", level),
                WordPair("3", "場所", "Place", level),
                WordPair("4", "人", "Person", level),
                WordPair("5", "仕事", "Work", level),
                WordPair("6", "友達", "Friend", level),
                WordPair("7", "家族", "Family", level),
                WordPair("8", "旅行", "Travel", level)
            )
        }
    }
}