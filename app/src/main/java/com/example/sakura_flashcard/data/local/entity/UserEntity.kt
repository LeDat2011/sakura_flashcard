package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing user data locally
 */
@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val avatar: String?,
    val createdAt: Instant,
    val lastLogin: Instant?,
    val flashcardsLearned: Int,
    val quizzesCompleted: Int,
    val currentStreak: Int,
    val totalStudyTimeMinutes: Long,
    val levelProgress: Map<JLPTLevel, Float>,
    val lastModified: Instant,
    val needsSync: Boolean = false
) {
    fun toUser(): User {
        return User(
            id = id,
            username = username,
            email = email,
            avatar = avatar,
            createdAt = createdAt,
            lastLogin = lastLogin,
            learningProgress = LearningProgress(
                flashcardsLearned = flashcardsLearned,
                quizzesCompleted = quizzesCompleted,
                currentStreak = currentStreak,
                totalStudyTimeMinutes = totalStudyTimeMinutes,
                levelProgress = levelProgress
            )
        )
    }
    
    companion object {
        fun fromUser(user: User, needsSync: Boolean = false): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                avatar = user.avatar,
                createdAt = user.createdAt,
                lastLogin = user.lastLogin,
                flashcardsLearned = user.learningProgress.flashcardsLearned,
                quizzesCompleted = user.learningProgress.quizzesCompleted,
                currentStreak = user.learningProgress.currentStreak,
                totalStudyTimeMinutes = user.learningProgress.totalStudyTimeMinutes,
                levelProgress = user.learningProgress.levelProgress,
                lastModified = Instant.now(),
                needsSync = needsSync
            )
        }
    }
}