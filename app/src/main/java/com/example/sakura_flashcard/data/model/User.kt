package com.example.sakura_flashcard.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.validation.ContentValidator
import java.time.Instant
import java.util.UUID

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val avatar: String? = null,
    val displayName: String? = null,
    val age: Int? = null,
    val currentLevel: JLPTLevel = JLPTLevel.N5,
    val targetLevel: JLPTLevel = JLPTLevel.N2,
    val dailyStudyGoalMinutes: Int = 15,
    val isOnboardingCompleted: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val lastLogin: Instant? = null,
    @Embedded
    val learningProgress: LearningProgress = LearningProgress()
) {
    init {
        require(ContentValidator.isValidUsername(username)) { 
            "Username must be 3-20 characters and contain only letters, numbers, and underscores" 
        }
        require(ContentValidator.isValidEmail(email)) { 
            "Invalid email format" 
        }
    }
    
    fun updateLastLogin(): User {
        return copy(lastLogin = Instant.now())
    }
    
    fun updateLearningProgress(progress: LearningProgress): User {
        return copy(learningProgress = progress)
    }
}

data class LearningProgress(
    val flashcardsLearned: Int = 0,
    val quizzesCompleted: Int = 0,
    val currentStreak: Int = 0,
    val totalStudyTimeMinutes: Long = 0,
    @TypeConverters(Converters::class)
    val levelProgress: Map<JLPTLevel, Float> = emptyMap()
) {
    init {
        require(flashcardsLearned >= 0) { "Flashcards learned cannot be negative" }
        require(quizzesCompleted >= 0) { "Quizzes completed cannot be negative" }
        require(currentStreak >= 0) { "Current streak cannot be negative" }
        require(totalStudyTimeMinutes >= 0) { "Total study time cannot be negative" }
        require(levelProgress.values.all { it in 0.0f..1.0f }) { 
            "Level progress must be between 0.0 and 1.0" 
        }
    }
    
    fun addFlashcardLearned(): LearningProgress {
        return copy(flashcardsLearned = flashcardsLearned + 1)
    }
    
    fun addQuizCompleted(): LearningProgress {
        return copy(quizzesCompleted = quizzesCompleted + 1)
    }
    
    fun incrementStreak(): LearningProgress {
        return copy(currentStreak = currentStreak + 1)
    }
    
    fun resetStreak(): LearningProgress {
        return copy(currentStreak = 0)
    }
    
    fun addStudyTime(minutes: Long): LearningProgress {
        return copy(totalStudyTimeMinutes = totalStudyTimeMinutes + minutes)
    }
    
    fun updateLevelProgress(level: JLPTLevel, progress: Float): LearningProgress {
        require(progress in 0.0f..1.0f) { "Progress must be between 0.0 and 1.0" }
        return copy(levelProgress = levelProgress + (level to progress))
    }
}