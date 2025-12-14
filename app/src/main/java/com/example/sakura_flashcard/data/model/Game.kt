package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant
import java.util.UUID

@Entity(tableName = "game_results")
@TypeConverters(Converters::class)
data class GameResult(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val gameType: GameType,
    val score: Int,
    val completedAt: Instant = Instant.now(),
    val timeSpentSeconds: Long,
    val level: JLPTLevel
) {
    init {
        require(userId.isNotBlank()) { "User ID cannot be empty" }
        require(score >= 0) { "Score cannot be negative" }
        require(timeSpentSeconds >= 0) { "Time spent cannot be negative" }
    }
    
    fun getScorePerSecond(): Float {
        return if (timeSpentSeconds > 0) score.toFloat() / timeSpentSeconds else 0f
    }
}

enum class GameType(val displayName: String, val description: String) {
    SENTENCE_ORDER_PUZZLE(
        "Ghép câu tiếng Nhật", 
        "Kéo thả từ để tạo câu tiếng Nhật hoàn chỉnh"
    ),
    QUICK_ANSWER_CHALLENGE(
        "Trả lời nhanh", 
        "Trả lời câu hỏi nhanh nhất có thể trong 5 giây"
    ),
    MEMORY_MATCH_GAME(
        "Ghép thẻ nhớ", 
        "Ghép từ tiếng Nhật với nghĩa bằng cách lật thẻ"
    );
    
    companion object {
        fun fromDisplayName(displayName: String): GameType? {
            return values().find { it.displayName == displayName }
        }
        
        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

interface MiniGame {
    val name: String
    val description: String
    val type: GameType
    fun startGame(): GameState
    fun processInput(input: GameInput): GameState
    fun getMaxScore(): Int
    fun getTimeLimit(): Long? // in seconds, null for no time limit
}

sealed class GameState {
    object NotStarted : GameState()
    
    data class InProgress(
        val currentScore: Int,
        val timeRemaining: Long?, // in seconds
        val currentQuestion: Any?,
        val questionsCompleted: Int = 0,
        val totalQuestions: Int = 10
    ) : GameState() {
        fun getProgress(): Float {
            return if (totalQuestions > 0) questionsCompleted.toFloat() / totalQuestions else 0f
        }
    }
    
    data class Completed(
        val finalScore: Int,
        val totalTime: Long, // in seconds
        val performance: GamePerformance,
        val questionsCompleted: Int,
        val totalQuestions: Int
    ) : GameState() {
        fun getCompletionRate(): Float {
            return if (totalQuestions > 0) questionsCompleted.toFloat() / totalQuestions else 0f
        }
    }
}

sealed class GameInput {
    data class WordOrder(val orderedWords: List<String>) : GameInput() {
        init {
            require(orderedWords.isNotEmpty()) { "Word order cannot be empty" }
            require(orderedWords.all { it.isNotBlank() }) { "All words must be non-empty" }
        }
    }
    
    data class Answer(val answer: String) : GameInput() {
        init {
            require(answer.isNotBlank()) { "Answer cannot be empty" }
        }
    }
    
    data class CardMatch(val card1: String, val card2: String) : GameInput() {
        init {
            require(card1.isNotBlank()) { "Card 1 cannot be empty" }
            require(card2.isNotBlank()) { "Card 2 cannot be empty" }
            require(card1 != card2) { "Cannot match a card with itself" }
        }
    }
}

data class GamePerformance(
    val accuracy: Float, // 0.0 to 1.0
    val speed: Float, // questions per second
    val improvement: Float // compared to previous attempts, -1.0 to 1.0
) {
    init {
        require(accuracy in 0.0f..1.0f) { "Accuracy must be between 0.0 and 1.0" }
        require(speed >= 0.0f) { "Speed cannot be negative" }
        require(improvement in -1.0f..1.0f) { "Improvement must be between -1.0 and 1.0" }
    }
    
    fun getAccuracyPercentage(): Float = accuracy * 100f
    
    fun getPerformanceRating(): String {
        return when {
            accuracy >= 0.9f && speed >= 0.5f -> "Xuất sắc"
            accuracy >= 0.8f && speed >= 0.3f -> "Tốt"
            accuracy >= 0.7f -> "Khá"
            else -> "Cần cải thiện"
        }
    }
}

data class GameStatistics(
    val totalGamesPlayed: Int = 0,
    val totalScore: Int = 0,
    val totalTimeSpent: Long = 0L, // in seconds
    val averageAccuracy: Float = 0f,
    val bestScores: Map<GameType, Int> = emptyMap(),
    val gameTypeStats: Map<GameType, GameTypeStatistics> = emptyMap()
) {
    fun getAverageScore(): Float {
        return if (totalGamesPlayed > 0) totalScore.toFloat() / totalGamesPlayed else 0f
    }
    
    fun getAverageTimePerGame(): Float {
        return if (totalGamesPlayed > 0) totalTimeSpent.toFloat() / totalGamesPlayed else 0f
    }
    
    fun getTotalTimeFormatted(): String {
        val hours = totalTimeSpent / 3600
        val minutes = (totalTimeSpent % 3600) / 60
        val seconds = totalTimeSpent % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}

data class GameTypeStatistics(
    val gamesPlayed: Int = 0,
    val averageScore: Float = 0f,
    val bestScore: Int = 0,
    val averageTime: Float = 0f // in seconds
) {
    fun getAverageTimeFormatted(): String {
        val minutes = (averageTime / 60).toInt()
        val seconds = (averageTime % 60).toInt()
        
        return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
    }
}