package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing spaced repetition data locally
 */
@Entity(tableName = "spaced_repetition")
@TypeConverters(Converters::class)
data class SpacedRepetitionEntity(
    @PrimaryKey
    val id: String, // userId_flashcardId
    val userId: String,
    val flashcardId: String,
    val repetitionCount: Int,
    val easeFactor: Float,
    val interval: Int,
    val nextReview: Instant,
    val lastReviewed: Instant?,
    val correctStreak: Int,
    val totalReviews: Int,
    val averageResponseTime: Long,
    val difficultyAdjustment: Float,
    val lastModified: Instant,
    val needsSync: Boolean = false
) {
    fun toSpacedRepetitionData(): SpacedRepetitionData {
        return SpacedRepetitionData(
            userId = userId,
            flashcardId = flashcardId,
            repetitionCount = repetitionCount,
            easeFactor = easeFactor,
            interval = interval,
            nextReview = nextReview,
            lastReviewed = lastReviewed,
            correctStreak = correctStreak,
            totalReviews = totalReviews,
            averageResponseTime = averageResponseTime,
            difficultyAdjustment = difficultyAdjustment
        )
    }
    
    companion object {
        fun fromSpacedRepetitionData(
            data: SpacedRepetitionData, 
            needsSync: Boolean = false
        ): SpacedRepetitionEntity {
            return SpacedRepetitionEntity(
                id = "${data.userId}_${data.flashcardId}",
                userId = data.userId,
                flashcardId = data.flashcardId,
                repetitionCount = data.repetitionCount,
                easeFactor = data.easeFactor,
                interval = data.interval,
                nextReview = data.nextReview,
                lastReviewed = data.lastReviewed,
                correctStreak = data.correctStreak,
                totalReviews = data.totalReviews,
                averageResponseTime = data.averageResponseTime,
                difficultyAdjustment = data.difficultyAdjustment,
                lastModified = Instant.now(),
                needsSync = needsSync
            )
        }
    }
}