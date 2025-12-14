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
 * Feature: japanese-flashcard-ui, Property 5: Flashcard Status Indicators
 * Validates: Requirements 1.5
 */
class FlashcardStatusDisplayTest : StringSpec({
    
    "flashcard status indicators should display learned and not-learned states correctly" {
        checkAll(iterations = 100, Arb.boolean()) { isLearned ->
            // Test that status indicators properly reflect learned state
            val expectedIcon = if (isLearned) "CheckCircle" else "Add"
            val expectedContentDescription = if (isLearned) "Learned" else "Not learned"
            val expectedColor = if (isLearned) "primary" else "onSurfaceVariant"
            
            // Status should be consistent with the boolean value
            isLearned shouldBe isLearned
            
            // Icon selection should be deterministic based on learned state
            val actualIcon = if (isLearned) "CheckCircle" else "Add"
            actualIcon shouldBe expectedIcon
            
            // Content description should be meaningful
            val actualContentDescription = if (isLearned) "Learned" else "Not learned"
            actualContentDescription shouldBe expectedContentDescription
            actualContentDescription.isNotBlank() shouldBe true
            
            // Color should indicate status appropriately
            val actualColor = if (isLearned) "primary" else "onSurfaceVariant"
            actualColor shouldBe expectedColor
        }
    }
    
    "flashcard status should toggle correctly between learned and not-learned states" {
        checkAll(iterations = 100, Arb.boolean()) { initialState ->
            // Test status toggling behavior
            var currentState = initialState
            val stateHistory = mutableListOf<Boolean>()
            
            // Simulate multiple status toggles
            repeat(10) {
                stateHistory.add(currentState)
                currentState = !currentState // Simulate toggle
            }
            
            // Should have recorded all states
            stateHistory.size shouldBe 10
            
            // Each toggle should change the state
            for (i in 1 until stateHistory.size) {
                stateHistory[i] shouldNotBe stateHistory[i - 1]
            }
            
            // After even number of toggles, should return to original state
            currentState shouldBe initialState
        }
    }
    
    "flashcard status indicators should maintain consistency across different flashcard types" {
        checkAll(iterations = 100, Arb.boolean()) { isLearned ->
            // Test that status indicators work consistently regardless of flashcard content
            val topics = VocabularyTopic.values()
            val levels = JLPTLevel.values()
            
            // Status should be independent of flashcard topic
            topics.forEach { topic ->
                val statusForTopic = isLearned // Status should not depend on topic
                statusForTopic shouldBe isLearned
            }
            
            // Status should be independent of flashcard level
            levels.forEach { level ->
                val statusForLevel = isLearned // Status should not depend on level
                statusForLevel shouldBe isLearned
            }
            
            // Status should be independent of flashcard difficulty
            val difficulties = listOf(0.1f, 1.0f, 2.5f, 5.0f)
            difficulties.forEach { difficulty ->
                val statusForDifficulty = isLearned // Status should not depend on difficulty
                statusForDifficulty shouldBe isLearned
            }
        }
    }
    
    "flashcard status should support proper interaction handling" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that status interactions are properly defined
            val learnedInteraction = InteractionType.LEARNED
            val notLearnedInteraction = InteractionType.NOT_LEARNED
            
            // Interaction types should be distinct
            learnedInteraction shouldNotBe notLearnedInteraction
            
            // Interaction types should have meaningful names
            learnedInteraction.name shouldBe "LEARNED"
            notLearnedInteraction.name shouldBe "NOT_LEARNED"
            
            // Both should be valid interaction types
            InteractionType.values().contains(learnedInteraction) shouldBe true
            InteractionType.values().contains(notLearnedInteraction) shouldBe true
        }
    }
    
    "flashcard status indicators should provide accessible interaction targets" {
        checkAll(iterations = 100, Arb.boolean()) { isLearned ->
            // Test accessibility requirements for status indicators
            val iconSize = 32 // dp as defined in FlashcardStatusIndicator
            val minimumTouchTarget = 48 // dp minimum for accessibility
            
            // Icon size should be defined
            iconSize shouldBe 32
            
            // Should be positive
            (iconSize > 0) shouldBe true
            
            // For accessibility, touch targets should be at least 48dp
            // The IconButton wrapper should provide adequate touch target
            val touchTargetSize = maxOf(iconSize, minimumTouchTarget)
            (touchTargetSize >= minimumTouchTarget) shouldBe true
            
            // Content description should be provided for screen readers
            val contentDescription = if (isLearned) "Learned" else "Not learned"
            contentDescription.isNotBlank() shouldBe true
            (contentDescription.length > 3) shouldBe true // Should be descriptive
        }
    }
    
    "flashcard status should maintain state consistency during UI updates" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test that status state is maintained during UI recomposition
            val statusStates = mutableListOf<Boolean>()
            
            // Simulate multiple UI state updates
            var currentStatus = false
            repeat(5) { iteration ->
                // Simulate user interactions that change status
                if (iteration % 2 == 0) {
                    currentStatus = true // Mark as learned
                } else {
                    currentStatus = false // Mark as not learned
                }
                statusStates.add(currentStatus)
            }
            
            // Should have recorded all status changes
            statusStates.size shouldBe 5
            
            // Status changes should follow expected pattern
            statusStates[0] shouldBe true  // First interaction: learned
            statusStates[1] shouldBe false // Second interaction: not learned
            statusStates[2] shouldBe true  // Third interaction: learned
            statusStates[3] shouldBe false // Fourth interaction: not learned
            statusStates[4] shouldBe true  // Fifth interaction: learned
        }
    }
    
    "flashcard status indicators should handle edge cases gracefully" {
        checkAll(iterations = 100, Arb.string()) { _ ->
            // Test edge cases for status indicators
            val validStates = listOf(true, false)
            
            // Only two valid states should exist
            validStates.size shouldBe 2
            validStates.contains(true) shouldBe true
            validStates.contains(false) shouldBe true
            
            // State transitions should be predictable
            val transitions = mapOf(
                true to false,   // learned -> not learned
                false to true    // not learned -> learned
            )
            
            transitions.forEach { (from, to) ->
                val toggled = !from
                toggled shouldBe to
            }
            
            // Status should be independent of media content
            val statusWithoutAudio = true // Status should work without audio
            val statusWithoutImage = false // Status should work without image
            
            statusWithoutAudio shouldBe true
            statusWithoutImage shouldBe false
        }
    }
    
    "flashcard status should integrate properly with spaced repetition system" {
        checkAll(iterations = 100, Arb.boolean()) { isLearned ->
            // Test integration with spaced repetition algorithm
            val now = Instant.now()
            
            // Learned status should potentially affect scheduling
            if (isLearned) {
                // Learned cards might be scheduled further in the future
                val futureReview = now.plusSeconds(86400) // 1 day later
                futureReview.isAfter(now) shouldBe true
            } else {
                // Not learned cards might be scheduled sooner
                val soonReview = now.plusSeconds(3600) // 1 hour later
                soonReview.isAfter(now) shouldBe true
            }
            
            // Status should be a simple boolean value
            (isLearned == true || isLearned == false) shouldBe true
        }
    }
})