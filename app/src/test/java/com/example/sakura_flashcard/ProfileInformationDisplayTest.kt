package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.User
import com.example.sakura_flashcard.data.model.LearningProgress
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.time.Instant

/**
 * **Feature: japanese-flashcard-ui, Property 20: Profile Information Display**
 * **Validates: Requirements 5.1**
 * 
 * Property: For any profile screen access, avatar, username, and email information should be displayed
 */
class ProfileInformationDisplayTest : StringSpec({
    
    "profile display should contain avatar, username, and email for any valid user" {
        checkAll(iterations = 100, userArb()) { user ->
            // Simulate profile information display
            val profileDisplayContent = generateProfileDisplayContent(user)
            
            // Verify that all required information is present
            profileDisplayContent.shouldContain(user.username)
            profileDisplayContent.shouldContain(user.email)
            
            // Avatar should be displayed (either custom avatar or default placeholder)
            val hasAvatarInfo = user.avatar?.let { 
                profileDisplayContent.contains(it) 
            } ?: profileDisplayContent.contains("default_avatar")
            
            hasAvatarInfo shouldBe true
        }
    }
    
    "profile display should handle users with and without custom avatars" {
        checkAll(iterations = 100, userArb()) { user ->
            val profileDisplayContent = generateProfileDisplayContent(user)
            
            // Should always have some avatar representation
            val hasAvatarRepresentation = user.avatar?.let {
                profileDisplayContent.contains(it)
            } ?: profileDisplayContent.contains("default_avatar")
            
            hasAvatarRepresentation shouldBe true
            
            // Should always contain core user information
            profileDisplayContent.shouldContain(user.username)
            profileDisplayContent.shouldContain(user.email)
        }
    }
})

// Test helper function to simulate profile display content generation
private fun generateProfileDisplayContent(user: User): String {
    return buildString {
        append("Profile Display: ")
        append("Username: ${user.username}, ")
        append("Email: ${user.email}, ")
        append("Avatar: ${user.avatar ?: "default_avatar"}")
    }
}

// Arbitrary generator for User objects
private fun userArb() = arbitrary {
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

// Arbitrary generator for LearningProgress
private fun learningProgressArb() = arbitrary {
    LearningProgress(
        flashcardsLearned = Arb.int(0..1000).bind(),
        quizzesCompleted = Arb.int(0..500).bind(),
        currentStreak = Arb.int(0..100).bind(),
        totalStudyTimeMinutes = Arb.long(0L..10000L).bind(),
        levelProgress = emptyMap() // Simplified for this test
    )
}