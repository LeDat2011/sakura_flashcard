package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing quiz results locally
 */
@Entity(tableName = "quiz_results")
@TypeConverters(Converters::class)
data class QuizResultEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val topic: VocabularyTopic,
    val level: JLPTLevel,
    val score: Int,
    val totalQuestions: Int,
    val completedAt: Instant,
    val timeSpentSeconds: Long,
    val answers: List<QuizAnswer>,
    val lastModified: Instant,
    val needsSync: Boolean = false
) {
    fun toQuizResult(): QuizResult {
        return QuizResult(
            id = id,
            userId = userId,
            topic = topic,
            level = level,
            score = score,
            totalQuestions = totalQuestions,
            completedAt = completedAt,
            timeSpentSeconds = timeSpentSeconds,
            answers = answers
        )
    }
    
    companion object {
        fun fromQuizResult(result: QuizResult, needsSync: Boolean = false): QuizResultEntity {
            return QuizResultEntity(
                id = result.id,
                userId = result.userId,
                topic = result.topic,
                level = result.level,
                score = result.score,
                totalQuestions = result.totalQuestions,
                completedAt = result.completedAt,
                timeSpentSeconds = result.timeSpentSeconds,
                answers = result.answers,
                lastModified = Instant.now(),
                needsSync = needsSync
            )
        }
    }
}