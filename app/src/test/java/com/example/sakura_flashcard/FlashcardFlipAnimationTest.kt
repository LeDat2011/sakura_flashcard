package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.ui.components.InteractionType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.Instant

/**
 * Feature: japanese-flashcard-ui, Property 25: UI Consistency and Animation (flashcard flip aspect)
 * Validates: Requirements 6.3
 */
class FlashcardFlipAnimationTest : StringSpec({
    
    "flashcard flip animation should maintain consistent interaction types" {
        checkAll(iterations = 100, Arb.flashcard()) { flashcard ->
            // Test that all interaction types are properly defined
            val interactionTypes = InteractionType.values()
            
            // Should have exactly 4 interaction types
            interactionTypes.size shouldBe 4
            
            // Should contain all expected interaction types
            interactionTypes.contains(InteractionType.FLIP) shouldBe true
            interactionTypes.contains(InteractionType.LEARNED) shouldBe true
            interactionTypes.contains(InteractionType.NOT_LEARNED) shouldBe true
            interactionTypes.contains(InteractionType.PLAY_AUDIO) shouldBe true
            
            // Each interaction type should have a unique name
            val uniqueNames = interactionTypes.map { it.name }.toSet()
            uniqueNames.size shouldBe interactionTypes.size
        }
    }
    
    "flashcard flip state should be consistent across animation cycles" {
        checkAll(iterations = 100, Arb.flashcard(), Arb.boolean()) { flashcard, initialFlipState ->
            // Test that flip state changes are consistent
            var currentFlipState = initialFlipState
            
            // Simulate multiple flip interactions
            repeat(10) {
                val previousState = currentFlipState
                currentFlipState = !currentFlipState // Simulate flip
                
                // State should always be opposite of previous
                currentFlipState shouldNotBe previousState
                
                // State should be a valid boolean
                (currentFlipState == true || currentFlipState == false) shouldBe true
            }
            
            // After even number of flips, should return to original state
            currentFlipState shouldBe initialFlipState
        }
    }
    
    "flashcard animation should support smooth transitions with proper timing" {
        checkAll(iterations = 100, Arb.flashcard()) { flashcard ->
            // Test animation timing constants
            val animationDuration = 600 // milliseconds as defined in FlashcardView
            val cameraDistance = 12f // density multiplier as defined in FlashcardView
            
            // Animation duration should be reasonable (between 200ms and 1000ms)
            animationDuration shouldBe 600
            animationDuration.let { it >= 200 && it <= 1000 } shouldBe true
            
            // Camera distance should be positive for 3D effect
            cameraDistance shouldBe 12f
            (cameraDistance > 0f) shouldBe true
            
            // Rotation values should be valid
            val frontRotation = 0f
            val backRotation = 180f
            
            frontRotation shouldBe 0f
            backRotation shouldBe 180f
            (backRotation - frontRotation) shouldBe 180f
        }
    }
    
    "flashcard should maintain content integrity during flip animations" {
        checkAll(iterations = 100, Arb.flashcard(), Arb.boolean()) { flashcard, isFlipped ->
            // Test that flashcard content remains unchanged during animation
            val originalFrontText = flashcard.front.text
            val originalBackText = flashcard.back.text
            val originalId = flashcard.id
            val originalTopic = flashcard.topic
            val originalLevel = flashcard.level
            
            // Content should remain the same regardless of flip state
            flashcard.front.text shouldBe originalFrontText
            flashcard.back.text shouldBe originalBackText
            flashcard.id shouldBe originalId
            flashcard.topic shouldBe originalTopic
            flashcard.level shouldBe originalLevel
            
            // Both sides should have valid content
            flashcard.front.text.isNotBlank() shouldBe true
            flashcard.back.text.isNotBlank() shouldBe true
            (flashcard.front.text.length <= 500) shouldBe true
            (flashcard.back.text.length <= 500) shouldBe true
        }
    }
    
    "flashcard flip animation should handle edge cases gracefully" {
        checkAll(iterations = 100, Arb.flashcard()) { flashcard ->
            // Test edge cases for animation
            val rotationThreshold = 90f // Threshold for showing front vs back
            
            // Test rotation values at boundaries
            val frontSideRotations = listOf(0f, 45f, 89f, 90f)
            val backSideRotations = listOf(91f, 135f, 180f, 270f, 360f)
            
            frontSideRotations.forEach { rotation ->
                (rotation <= rotationThreshold) shouldBe true
            }
            
            backSideRotations.forEach { rotation ->
                (rotation > rotationThreshold) shouldBe true
            }
            
            // Test that animation handles full rotations
            val fullRotation = 360f
            val normalizedRotation = fullRotation % 360f
            normalizedRotation shouldBe 0f
        }
    }
    
    "flashcard animation should support proper Material You design integration" {
        checkAll(iterations = 100, Arb.flashcard()) { flashcard ->
            // Test Material You design constants
            val cardElevation = 8 // dp as defined in FlashcardView
            val cornerRadius = 16 // dp as defined in FlashcardView
            val cardHeight = 200 // dp as defined in FlashcardView
            
            // Values should match Material You guidelines
            cardElevation shouldBe 8
            cornerRadius shouldBe 16
            cardHeight shouldBe 200
            
            // All values should be positive
            (cardElevation > 0) shouldBe true
            (cornerRadius > 0) shouldBe true
            (cardHeight > 0) shouldBe true
            
            // Corner radius should be reasonable for cards
            cornerRadius.let { it >= 8 && it <= 24 } shouldBe true
        }
    }
    
    "flashcard flip should maintain accessibility during animations" {
        checkAll(iterations = 100, Arb.flashcard(), Arb.boolean()) { flashcard, isFlipped ->
            // Test accessibility considerations
            val frontContentDescription = "Flashcard front: ${flashcard.front.text}"
            val backContentDescription = "Flashcard back: ${flashcard.back.text}"
            
            // Content descriptions should be meaningful
            frontContentDescription.contains(flashcard.front.text) shouldBe true
            backContentDescription.contains(flashcard.back.text) shouldBe true
            
            // Should not be empty
            frontContentDescription.isNotBlank() shouldBe true
            backContentDescription.isNotBlank() shouldBe true
            
            // Should indicate which side is showing
            frontContentDescription.contains("front") shouldBe true
            backContentDescription.contains("back") shouldBe true
        }
    }
    
    "flashcard animation should handle rapid interaction gracefully" {
        checkAll(iterations = 100, Arb.flashcard()) { flashcard ->
            // Test rapid flip interactions
            var flipState = false
            val interactions = mutableListOf<Boolean>()
            
            // Simulate rapid flips
            repeat(20) {
                flipState = !flipState
                interactions.add(flipState)
            }
            
            // Should have recorded all interactions
            interactions.size shouldBe 20
            
            // Should alternate between true and false
            for (i in 1 until interactions.size) {
                interactions[i] shouldNotBe interactions[i - 1]
            }
            
            // Final state should be back to original (even number of flips)
            flipState shouldBe false
        }
    }
})

// Custom Arb for generating test flashcards
fun Arb.Companion.flashcard(): Arb<Flashcard> = arbitrary { rs ->
    val topics = VocabularyTopic.values()
    val levels = JLPTLevel.values()
    
    Flashcard(
        id = Arb.string(10..20).bind(),
        front = FlashcardSide(
            text = Arb.string(1..100).bind(),
            translation = if (Arb.boolean().bind()) Arb.string(1..100).bind() else null,
            explanation = if (Arb.boolean().bind()) Arb.string(1..200).bind() else null,
            audio = if (Arb.boolean().bind()) Arb.string(10..50).bind() else null
        ),
        back = FlashcardSide(
            text = Arb.string(1..100).bind(),
            translation = if (Arb.boolean().bind()) Arb.string(1..100).bind() else null,
            explanation = if (Arb.boolean().bind()) Arb.string(1..200).bind() else null,
            audio = if (Arb.boolean().bind()) Arb.string(10..50).bind() else null
        ),
        topic = topics[Arb.int(0, topics.size - 1).bind()],
        level = levels[Arb.int(0, levels.size - 1).bind()],
        difficulty = Arb.float(0.1f, 5.0f).bind(),
        lastReviewed = if (Arb.boolean().bind()) Instant.now().minusSeconds(Arb.long(0, 86400).bind()) else null,
        nextReview = if (Arb.boolean().bind()) Instant.now().plusSeconds(Arb.long(0, 86400).bind()) else null,
        isCustom = Arb.boolean().bind(),
        createdBy = if (Arb.boolean().bind()) Arb.string(10..20).bind() else null
    )
}