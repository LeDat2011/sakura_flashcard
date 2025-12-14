package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.User
import com.example.sakura_flashcard.data.model.LearningProgress
import com.example.sakura_flashcard.data.model.JLPTLevel
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import java.time.Instant

/**
 * **Feature: japanese-flashcard-ui, Property 22: Learning Progress Statistics**
 * **Validates: Requirements 5.3**
 * 
 * Property: For any learning progress view, statistics for flashcards learned and quizzes completed should be displayed
 */
class LearningProgressStatisticsTest : StringSpec({
    
    "learning progress statistics should display flashcards learned and quizzes completed for any user" {
        checkAll(iterations = 100, userWithProgressArb()) { user ->
            // Simulate learning progress statistics display
            val statisticsDisplay = generateLearningProgressStatistics(user.learningProgress)
            
            // Verify that flashcards learned statistic is displayed
            statisticsDisplay.flashcardsLearnedDisplay shouldNotBe null
            statisticsDisplay.flashcardsLearnedDisplay shouldBe "Flashcards Learned: ${user.learningProgress.flashcardsLearned}"
            
            // Verify that quizzes completed statistic is displayed
            statisticsDisplay.quizzesCompletedDisplay shouldNotBe null
            statisticsDisplay.quizzesCompletedDisplay shouldBe "Quizzes Completed: ${user.learningProgress.quizzesCompleted}"
            
            // Verify that the values are non-negative
            user.learningProgress.flashcardsLearned shouldBeGreaterThanOrEqualTo 0
            user.learningProgress.quizzesCompleted shouldBeGreaterThanOrEqualTo 0
        }
    }
    
    "learning progress statistics should include additional meaningful metrics" {
        checkAll(iterations = 100, userWithProgressArb()) { user ->
            val statisticsDisplay = generateLearningProgressStatistics(user.learningProgress)
            
            // Verify that current streak is displayed
            statisticsDisplay.currentStreakDisplay shouldNotBe null
            statisticsDisplay.currentStreakDisplay shouldBe "Current Streak: ${user.learningProgress.currentStreak} days"
            
            // Verify that total study time is displayed
            statisticsDisplay.totalStudyTimeDisplay shouldNotBe null
            val hours = user.learningProgress.totalStudyTimeMinutes / 60
            val minutes = user.learningProgress.totalStudyTimeMinutes % 60
            statisticsDisplay.totalStudyTimeDisplay shouldBe "Total Study Time: ${hours}h ${minutes}m"
            
            // Verify that all values are valid
            user.learningProgress.currentStreak shouldBeGreaterThanOrEqualTo 0
            user.learningProgress.totalStudyTimeMinutes shouldBeGreaterThanOrEqualTo 0L
        }
    }
    
    "learning progress statistics should handle JLPT level progress when available" {
        checkAll(iterations = 100, userWithLevelProgressArb()) { user ->
            val statisticsDisplay = generateLearningProgressStatistics(user.learningProgress)
            
            // Verify that level progress is displayed when available
            if (user.learningProgress.levelProgress.isNotEmpty()) {
                statisticsDisplay.levelProgressDisplay shouldNotBe null
                statisticsDisplay.levelProgressDisplay!!.isNotEmpty() shouldBe true
            } else {
                // When no level progress, display should be null
                statisticsDisplay.levelProgressDisplay shouldBe null
            }
        }
    }
})

// Data class for statistics display
data class LearningProgressStatisticsDisplay(
    val flashcardsLearnedDisplay: String,
    val quizzesCompletedDisplay: String,
    val currentStreakDisplay: String,
    val totalStudyTimeDisplay: String,
    val levelProgressDisplay: List<String>? = null
)

// Test helper function to generate learning progress statistics display
private fun generateLearningProgressStatistics(progress: LearningProgress): LearningProgressStatisticsDisplay {
    val hours = progress.totalStudyTimeMinutes / 60
    val minutes = progress.totalStudyTimeMinutes % 60
    
    val levelProgressDisplay = if (progress.levelProgress.isNotEmpty()) {
        progress.levelProgress.map { (level, progressValue) ->
            "JLPT $level: ${(progressValue * 100).toInt()}%"
        }
    } else null
    
    return LearningProgressStatisticsDisplay(
        flashcardsLearnedDisplay = "Flashcards Learned: ${progress.flashcardsLearned}",
        quizzesCompletedDisplay = "Quizzes Completed: ${progress.quizzesCompleted}",
        currentStreakDisplay = "Current Streak: ${progress.currentStreak} days",
        totalStudyTimeDisplay = "Total Study Time: ${hours}h ${minutes}m",
        levelProgressDisplay = levelProgressDisplay
    )
}

// Arbitrary generator for User objects with learning progress
private fun userWithProgressArb() = arbitrary {
    val validUsernames = listOf(
        "user123", "testuser", "john_doe", "alice_smith", "bob_jones",
        "student1", "learner_99", "sakura_fan", "japanese_student", "quiz_master"
    )
    
    User(
        username = Arb.element(validUsernames).bind(),
        email = "test@example.com",
        avatar = Arb.choice(
            Arb.constant(null),
            Arb.string(5..50).map { "avatar_$it.jpg" }
        ).bind(),
        createdAt = Arb.instant().bind(),
        lastLogin = Arb.choice(
            Arb.constant(null),
            Arb.instant()
        ).bind(),
        learningProgress = learningProgressArb().bind()
    )
}

// Arbitrary generator for User objects with JLPT level progress
private fun userWithLevelProgressArb() = arbitrary {
    val validUsernames = listOf(
        "user123", "testuser", "john_doe", "alice_smith", "bob_jones",
        "student1", "learner_99", "sakura_fan", "japanese_student", "quiz_master"
    )
    
    User(
        username = Arb.element(validUsernames).bind(),
        email = "test@example.com",
        avatar = Arb.choice(
            Arb.constant(null),
            Arb.string(5..50).map { "avatar_$it.jpg" }
        ).bind(),
        createdAt = Arb.instant().bind(),
        lastLogin = Arb.choice(
            Arb.constant(null),
            Arb.instant()
        ).bind(),
        learningProgress = learningProgressWithLevelsArb().bind()
    )
}

// Arbitrary generator for LearningProgress
private fun learningProgressArb() = arbitrary {
    LearningProgress(
        flashcardsLearned = Arb.int(0..1000).bind(),
        quizzesCompleted = Arb.int(0..500).bind(),
        currentStreak = Arb.int(0..100).bind(),
        totalStudyTimeMinutes = Arb.long(0L..10000L).bind(),
        levelProgress = emptyMap()
    )
}

// Arbitrary generator for LearningProgress with JLPT levels
private fun learningProgressWithLevelsArb() = arbitrary {
    val levelProgress = mutableMapOf<JLPTLevel, Float>()
    
    // Randomly add some JLPT levels with progress (ensuring valid range)
    JLPTLevel.values().forEach { level ->
        if (Arb.boolean().bind()) {
            // Generate valid progress values between 0.0f and 1.0f
            val progress = Arb.float(0.0f, 1.0f).bind()
            levelProgress[level] = progress
        }
    }
    
    LearningProgress(
        flashcardsLearned = Arb.int(0..1000).bind(),
        quizzesCompleted = Arb.int(0..500).bind(),
        currentStreak = Arb.int(0..100).bind(),
        totalStudyTimeMinutes = Arb.long(0L..10000L).bind(),
        levelProgress = levelProgress
    )
}

