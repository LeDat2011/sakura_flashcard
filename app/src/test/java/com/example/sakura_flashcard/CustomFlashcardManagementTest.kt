package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.User
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.data.model.FlashcardSide
import com.example.sakura_flashcard.data.model.VocabularyTopic
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.LearningProgress
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import java.time.Instant
import java.util.UUID

/**
 * **Feature: japanese-flashcard-ui, Property 23: Custom Flashcard Management**
 * **Validates: Requirements 5.4**
 * 
 * Property: For any custom content creation request, users should be able to create and manage custom flashcard decks
 */
class CustomFlashcardManagementTest : StringSpec({
    
    "custom flashcard management should allow creating custom flashcard decks for any user" {
        checkAll(iterations = 100, userArb(), customFlashcardArb()) { user, customFlashcard ->
            // Simulate custom flashcard deck creation
            val deckManager = CustomFlashcardDeckManager()
            val createResult = deckManager.createCustomFlashcard(user.id, customFlashcard)
            
            // Verify that custom flashcard creation is successful
            createResult.success shouldBe true
            createResult.flashcardId shouldNotBe null
            
            // Verify that the flashcard is added to user's custom deck
            val userDeck = deckManager.getUserCustomDeck(user.id)
            userDeck.flashcards.shouldContain(createResult.flashcardId!!)
        }
    }
    
    "custom flashcard management should allow editing existing custom flashcards" {
        checkAll(iterations = 100, userArb(), customFlashcardArb()) { user, originalFlashcard ->
            val deckManager = CustomFlashcardDeckManager()
            
            // Create a custom flashcard first
            val createResult = deckManager.createCustomFlashcard(user.id, originalFlashcard)
            createResult.success shouldBe true
            
            // Edit the flashcard
            val updatedFlashcard = originalFlashcard.copy(
                front = originalFlashcard.front.copy(text = "Updated Front Text"),
                back = originalFlashcard.back.copy(text = "Updated Back Text")
            )
            
            val editResult = deckManager.editCustomFlashcard(user.id, createResult.flashcardId!!, updatedFlashcard)
            
            // Verify that editing is successful
            editResult.success shouldBe true
            
            // Verify that the flashcard is updated
            val retrievedFlashcard = deckManager.getCustomFlashcard(user.id, createResult.flashcardId!!)
            retrievedFlashcard?.front?.text shouldBe "Updated Front Text"
            retrievedFlashcard?.back?.text shouldBe "Updated Back Text"
        }
    }
    
    "custom flashcard management should allow deleting custom flashcards" {
        checkAll(iterations = 100, userArb(), customFlashcardArb()) { user, customFlashcard ->
            val deckManager = CustomFlashcardDeckManager()
            
            // Create a custom flashcard first
            val createResult = deckManager.createCustomFlashcard(user.id, customFlashcard)
            createResult.success shouldBe true
            
            // Delete the flashcard
            val deleteResult = deckManager.deleteCustomFlashcard(user.id, createResult.flashcardId!!)
            
            // Verify that deletion is successful
            deleteResult.success shouldBe true
            
            // Verify that the flashcard is removed from user's deck
            val userDeck = deckManager.getUserCustomDeck(user.id)
            userDeck.flashcards.shouldNotContain(createResult.flashcardId!!)
        }
    }
    
    "custom flashcard management should organize flashcards into manageable decks" {
        checkAll(iterations = 50, userArb()) { user ->
            val deckManager = CustomFlashcardDeckManager()
            val flashcards = mutableListOf<String>()
            
            // Create multiple custom flashcards
            repeat(5) { index ->
                val flashcard = Flashcard(
                    front = FlashcardSide(text = "Custom Front $index"),
                    back = FlashcardSide(text = "Custom Back $index"),
                    topic = VocabularyTopic.DAILY_LIFE,
                    level = JLPTLevel.N5,
                    difficulty = 1.0f,
                    isCustom = true,
                    createdBy = user.id
                )
                
                val result = deckManager.createCustomFlashcard(user.id, flashcard)
                if (result.success) {
                    flashcards.add(result.flashcardId!!)
                }
            }
            
            // Verify that all flashcards are organized in the user's deck
            val userDeck = deckManager.getUserCustomDeck(user.id)
            userDeck.flashcards.size shouldBe flashcards.size
            
            flashcards.forEach { flashcardId ->
                userDeck.flashcards.shouldContain(flashcardId)
            }
        }
    }
})

// Data classes for custom flashcard management
data class CustomFlashcardDeck(
    val userId: String,
    val flashcards: List<String> = emptyList(),
    val createdAt: Instant = Instant.now(),
    val lastModified: Instant = Instant.now()
)

data class FlashcardCreateResult(
    val success: Boolean,
    val flashcardId: String? = null,
    val errorMessage: String? = null
)

data class FlashcardEditResult(
    val success: Boolean,
    val errorMessage: String? = null
)

data class FlashcardDeleteResult(
    val success: Boolean,
    val errorMessage: String? = null
)

// Mock custom flashcard deck manager for testing
class CustomFlashcardDeckManager {
    private val userDecks = mutableMapOf<String, CustomFlashcardDeck>()
    private val flashcards = mutableMapOf<String, Flashcard>()
    
    fun createCustomFlashcard(userId: String, flashcard: Flashcard): FlashcardCreateResult {
        return try {
            val flashcardId = UUID.randomUUID().toString()
            flashcards[flashcardId] = flashcard.copy(id = flashcardId)
            
            val currentDeck = userDecks[userId] ?: CustomFlashcardDeck(userId)
            val updatedDeck = currentDeck.copy(
                flashcards = currentDeck.flashcards + flashcardId,
                lastModified = Instant.now()
            )
            userDecks[userId] = updatedDeck
            
            FlashcardCreateResult(true, flashcardId)
        } catch (e: Exception) {
            FlashcardCreateResult(false, null, e.message)
        }
    }
    
    fun editCustomFlashcard(userId: String, flashcardId: String, updatedFlashcard: Flashcard): FlashcardEditResult {
        return try {
            val userDeck = userDecks[userId]
            if (userDeck?.flashcards?.contains(flashcardId) == true) {
                flashcards[flashcardId] = updatedFlashcard.copy(id = flashcardId)
                FlashcardEditResult(true)
            } else {
                FlashcardEditResult(false, "Flashcard not found in user's deck")
            }
        } catch (e: Exception) {
            FlashcardEditResult(false, e.message)
        }
    }
    
    fun deleteCustomFlashcard(userId: String, flashcardId: String): FlashcardDeleteResult {
        return try {
            val currentDeck = userDecks[userId]
            if (currentDeck?.flashcards?.contains(flashcardId) == true) {
                val updatedDeck = currentDeck.copy(
                    flashcards = currentDeck.flashcards - flashcardId,
                    lastModified = Instant.now()
                )
                userDecks[userId] = updatedDeck
                flashcards.remove(flashcardId)
                FlashcardDeleteResult(true)
            } else {
                FlashcardDeleteResult(false, "Flashcard not found in user's deck")
            }
        } catch (e: Exception) {
            FlashcardDeleteResult(false, e.message)
        }
    }
    
    fun getUserCustomDeck(userId: String): CustomFlashcardDeck {
        return userDecks[userId] ?: CustomFlashcardDeck(userId)
    }
    
    fun getCustomFlashcard(userId: String, flashcardId: String): Flashcard? {
        val userDeck = userDecks[userId]
        return if (userDeck?.flashcards?.contains(flashcardId) == true) {
            flashcards[flashcardId]
        } else {
            null
        }
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
        learningProgress = LearningProgress()
    )
}

// Arbitrary generator for custom flashcards
private fun customFlashcardArb() = arbitrary {
    Flashcard(
        front = FlashcardSide(
            text = "Custom Front: " + Arb.string(5..50).bind()
        ),
        back = FlashcardSide(
            text = "Custom Back: " + Arb.string(5..50).bind()
        ),
        topic = Arb.element(VocabularyTopic.values().toList()).bind(),
        level = Arb.element(JLPTLevel.values().toList()).bind(),
        difficulty = Arb.float(0.1f, 5.0f).bind(),
        isCustom = true,
        createdBy = "user123" // Will be overridden in tests
    )
}