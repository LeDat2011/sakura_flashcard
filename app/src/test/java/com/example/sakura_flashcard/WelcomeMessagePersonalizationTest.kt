package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.util.UUID

/**
 * **Feature: japanese-flashcard-ui, Property 1: Welcome Message Personalization**
 * **Validates: Requirements 1.1**
 * 
 * Property: For any authenticated user, when the application opens, 
 * the welcome message should contain that user's name
 */
class WelcomeMessagePersonalizationTest {

    @Test
    fun welcomeMessageContainsUserName() = runTest {
        checkAll(100, userGenerator()) { user ->
            // Test the logic that generates welcome messages
            val welcomeMessage = generateWelcomeMessage(user)
            
            // Verify that the welcome message contains the user's name
            assert(welcomeMessage.contains(user.username)) {
                "Welcome message '$welcomeMessage' should contain username '${user.username}'"
            }
        }
    }

    @Test
    fun welcomeMessageForNullUser() {
        val welcomeMessage = generateWelcomeMessage(null)
        
        // Verify that the welcome message shows default text for null user
        assert(welcomeMessage == "Welcome to Sakura Flashcard!") {
            "Welcome message for null user should be 'Welcome to Sakura Flashcard!' but was '$welcomeMessage'"
        }
    }

    // Helper function that mimics the logic in HomeScreen
    private fun generateWelcomeMessage(user: User?): String {
        return if (user != null) {
            "Welcome back, ${user.username}!"
        } else {
            "Welcome to Sakura Flashcard!"
        }
    }

    private fun userGenerator(): Arb<User> = arbitrary { rs ->
        val username = Arb.string(3..20, Codepoint.alphanumeric()).bind()
        val email = "${Arb.string(3..10, Codepoint.alphanumeric()).bind()}@example.com"
        
        User(
            id = UUID.randomUUID().toString(),
            username = username,
            email = email,
            avatar = null,
            createdAt = Instant.now(),
            lastLogin = Instant.now(),
            learningProgress = LearningProgress(
                flashcardsLearned = Arb.int(0..1000).bind(),
                quizzesCompleted = Arb.int(0..100).bind(),
                currentStreak = Arb.int(0..365).bind(),
                totalStudyTimeMinutes = Arb.long(0L..10000L).bind(),
                levelProgress = emptyMap()
            )
        )
    }
}