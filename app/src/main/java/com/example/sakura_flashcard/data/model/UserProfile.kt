package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * User profile information collected during onboarding
 */
@Entity(tableName = "user_profiles")
@TypeConverters(Converters::class)
data class UserProfile(
    @PrimaryKey
    val userId: String,
    val displayName: String,
    val age: Int? = null,
    val currentLevel: JLPTLevel = JLPTLevel.N5,
    val targetLevel: JLPTLevel = JLPTLevel.N3,
    val dailyStudyGoalMinutes: Int = 15,
    val isOnboardingCompleted: Boolean = false
)

/**
 * Available daily study time options
 */
enum class DailyStudyGoal(val minutes: Int, val displayName: String) {
    FIVE_MINUTES(5, "5 phút"),
    TEN_MINUTES(10, "10 phút"),
    FIFTEEN_MINUTES(15, "15 phút"),
    TWENTY_MINUTES(20, "20 phút"),
    THIRTY_MINUTES(30, "30 phút"),
    FORTY_FIVE_MINUTES(45, "45 phút"),
    ONE_HOUR(60, "1 giờ");

    companion object {
        fun fromMinutes(minutes: Int): DailyStudyGoal {
            return values().find { it.minutes == minutes } ?: FIFTEEN_MINUTES
        }
    }
}
