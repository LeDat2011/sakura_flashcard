package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

/**
 * Represents the spaced repetition data for a specific flashcard and user combination.
 * Based on the SuperMemo SM-2 algorithm with modifications for mobile learning.
 */
@Entity(tableName = "spaced_repetition")
@TypeConverters(Converters::class)
data class SpacedRepetitionData(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val flashcardId: String,
    val repetitionCount: Int = 0,
    val easeFactor: Float = 2.5f, // Initial ease factor (SM-2 default)
    val interval: Int = 1, // Days until next review
    val nextReview: Instant = Instant.now(),
    val lastReviewed: Instant? = null,
    val correctStreak: Int = 0,
    val totalReviews: Int = 0,
    val averageResponseTime: Long = 0L, // milliseconds
    val difficultyAdjustment: Float = 0.0f // Additional difficulty based on user performance
) {
    init {
        require(easeFactor >= 1.3f) { "Ease factor must be at least 1.3" }
        require(interval >= 1) { "Interval must be at least 1 day" }
        require(repetitionCount >= 0) { "Repetition count cannot be negative" }
        require(correctStreak >= 0) { "Correct streak cannot be negative" }
        require(totalReviews >= 0) { "Total reviews cannot be negative" }
        require(averageResponseTime >= 0) { "Average response time cannot be negative" }
    }
    
    /**
     * Updates the spaced repetition data based on user performance.
     * @param quality Quality of response (0-5, where 3+ is considered correct)
     * @param responseTime Time taken to respond in milliseconds
     * @return Updated SpacedRepetitionData
     */
    fun updateAfterReview(quality: Int, responseTime: Long = 0L): SpacedRepetitionData {
        require(quality in 0..5) { "Quality must be between 0 and 5" }
        
        val now = Instant.now()
        val isCorrect = quality >= 3
        
        // Update ease factor using SM-2 algorithm
        val newEaseFactor = if (isCorrect) {
            max(1.3f, easeFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)))
        } else {
            max(1.3f, easeFactor - 0.2f)
        }
        
        // Calculate new interval
        val newInterval = calculateNewInterval(quality, isCorrect)
        
        // Update streaks and counters
        val newCorrectStreak = if (isCorrect) correctStreak + 1 else 0
        val newRepetitionCount = if (isCorrect) repetitionCount + 1 else 0
        val newTotalReviews = totalReviews + 1
        
        // Update average response time
        val newAverageResponseTime = if (responseTime > 0 && totalReviews > 0) {
            ((averageResponseTime * totalReviews) + responseTime) / (totalReviews + 1)
        } else if (responseTime > 0) {
            responseTime
        } else {
            averageResponseTime
        }
        
        // Calculate difficulty adjustment based on performance patterns
        val newDifficultyAdjustment = calculateDifficultyAdjustment(
            quality, 
            responseTime, 
            newCorrectStreak, 
            newAverageResponseTime
        )
        
        return copy(
            repetitionCount = newRepetitionCount,
            easeFactor = newEaseFactor,
            interval = newInterval,
            nextReview = now.plusSeconds(newInterval * 24 * 3600L), // Convert days to seconds
            lastReviewed = now,
            correctStreak = newCorrectStreak,
            totalReviews = newTotalReviews,
            averageResponseTime = newAverageResponseTime,
            difficultyAdjustment = newDifficultyAdjustment
        )
    }
    
    private fun calculateNewInterval(quality: Int, isCorrect: Boolean): Int {
        return when {
            !isCorrect -> 1 // Reset to 1 day if incorrect
            repetitionCount == 0 -> 1 // First correct answer
            repetitionCount == 1 -> 6 // Second correct answer
            else -> (interval * easeFactor).toInt().coerceAtLeast(1)
        }
    }
    
    private fun calculateDifficultyAdjustment(
        quality: Int,
        responseTime: Long,
        currentStreak: Int,
        avgResponseTime: Long
    ): Float {
        var adjustment = difficultyAdjustment
        
        // Adjust based on response quality
        when (quality) {
            0, 1 -> adjustment += 0.2f // Very difficult
            2 -> adjustment += 0.1f // Difficult
            3 -> adjustment += 0.0f // Normal
            4 -> adjustment -= 0.05f // Easy
            5 -> adjustment -= 0.1f // Very easy
        }
        
        // Adjust based on response time (if available)
        if (responseTime > 0 && avgResponseTime > 0) {
            val timeRatio = responseTime.toFloat() / avgResponseTime
            when {
                timeRatio > 2.0f -> adjustment += 0.1f // Much slower than average
                timeRatio > 1.5f -> adjustment += 0.05f // Slower than average
                timeRatio < 0.5f -> adjustment -= 0.05f // Much faster than average
                timeRatio < 0.7f -> adjustment -= 0.02f // Faster than average
            }
        }
        
        // Adjust based on streak
        when {
            currentStreak >= 10 -> adjustment -= 0.1f
            currentStreak >= 5 -> adjustment -= 0.05f
            currentStreak == 0 && totalReviews > 3 -> adjustment += 0.1f
        }
        
        // Keep adjustment within reasonable bounds
        return adjustment.coerceIn(-1.0f, 1.0f)
    }
    
    /**
     * Checks if this flashcard is due for review
     */
    fun isDueForReview(): Boolean {
        return nextReview.isBefore(Instant.now()) || nextReview == Instant.now()
    }
    
    /**
     * Gets the priority score for this flashcard (higher = more urgent)
     */
    fun getPriorityScore(): Float {
        val now = Instant.now()
        val overdueDays = if (nextReview.isBefore(now)) {
            (now.epochSecond - nextReview.epochSecond) / (24 * 3600)
        } else {
            0L
        }
        
        // Base priority on how overdue the card is
        var priority = overdueDays.toFloat()
        
        // Increase priority for cards with low ease factor (difficult cards)
        priority += (2.5f - easeFactor) * 2
        
        // Increase priority for cards with difficulty adjustment
        priority += difficultyAdjustment * 3
        
        // Decrease priority for cards with high correct streak
        priority -= (correctStreak * 0.1f)
        
        return max(0f, priority)
    }
    
    /**
     * Gets the estimated mastery level (0.0 to 1.0)
     */
    fun getMasteryLevel(): Float {
        if (totalReviews == 0) return 0f
        
        val correctRate = correctStreak.toFloat() / totalReviews
        val easeBonus = (easeFactor - 1.3f) / (3.0f - 1.3f) // Normalize ease factor
        val intervalBonus = min(interval / 30f, 1f) // Cap at 30 days
        
        return ((correctRate * 0.5f) + (easeBonus * 0.3f) + (intervalBonus * 0.2f)).coerceIn(0f, 1f)
    }
}

/**
 * Enum representing the quality of user response in spaced repetition
 */
enum class ReviewQuality(val value: Int, val description: String) {
    BLACKOUT(0, "Complete blackout - no memory of the answer"),
    INCORRECT_EASY(1, "Incorrect response but answer seemed easy"),
    INCORRECT_HARD(2, "Incorrect response and answer seemed hard"),
    CORRECT_HARD(3, "Correct response but with serious difficulty"),
    CORRECT_HESITANT(4, "Correct response but with some hesitation"),
    CORRECT_EASY(5, "Correct response with perfect recall");
    
    companion object {
        fun fromValue(value: Int): ReviewQuality? {
            return values().find { it.value == value }
        }
    }
    
    fun isCorrect(): Boolean = value >= 3
}