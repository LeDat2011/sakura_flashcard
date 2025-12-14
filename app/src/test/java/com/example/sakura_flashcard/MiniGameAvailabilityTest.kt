package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.GameType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 15: Mini-Game Availability**
 * **Validates: Requirements 4.1**
 * 
 * Property: For any game screen access, exactly three distinct mini-games should be available:
 * Sentence Order Puzzle, Quick Answer Challenge, and Memory Match Game
 */
class MiniGameAvailabilityTest : StringSpec({

    "Property 15: Mini-Game Availability - exactly three distinct mini-games should be available" {
        checkAll(100, Arb.constant(Unit)) { _ ->
            // Test that GameType enum contains exactly the three required games
            val availableGameTypes = GameType.values().toList()
            
            // Should have exactly 3 games
            availableGameTypes.size shouldBe 3
            
            // Should contain the exact three required games
            val expectedGameTypes = listOf(
                GameType.SENTENCE_ORDER_PUZZLE,
                GameType.QUICK_ANSWER_CHALLENGE,
                GameType.MEMORY_MATCH_GAME
            )
            
            availableGameTypes shouldContainExactlyInAnyOrder expectedGameTypes
            
            // Verify display names match requirements
            GameType.SENTENCE_ORDER_PUZZLE.displayName shouldBe "Sentence Order Puzzle"
            GameType.QUICK_ANSWER_CHALLENGE.displayName shouldBe "Quick Answer Challenge"
            GameType.MEMORY_MATCH_GAME.displayName shouldBe "Memory Match Game"
            
            // Verify descriptions match requirements
            GameType.SENTENCE_ORDER_PUZZLE.description shouldBe "Drag words to form correct Japanese sentences"
            GameType.QUICK_ANSWER_CHALLENGE.description shouldBe "Answer questions as fast as possible with 5-second timer"
            GameType.MEMORY_MATCH_GAME.description shouldBe "Match Japanese words with their meanings by flipping cards"
        }
    }

    "Property 15: Mini-Game Availability - getAllDisplayNames returns all three games" {
        checkAll(100, Arb.constant(Unit)) { _ ->
            val displayNames = GameType.getAllDisplayNames()
            
            displayNames.size shouldBe 3
            displayNames shouldContainExactlyInAnyOrder listOf(
                "Sentence Order Puzzle",
                "Quick Answer Challenge", 
                "Memory Match Game"
            )
        }
    }

    "Property 15: Mini-Game Availability - fromDisplayName works for all games" {
        checkAll(100, Arb.constant(Unit)) { _ ->
            GameType.fromDisplayName("Sentence Order Puzzle") shouldBe GameType.SENTENCE_ORDER_PUZZLE
            GameType.fromDisplayName("Quick Answer Challenge") shouldBe GameType.QUICK_ANSWER_CHALLENGE
            GameType.fromDisplayName("Memory Match Game") shouldBe GameType.MEMORY_MATCH_GAME
            GameType.fromDisplayName("Non-existent Game") shouldBe null
        }
    }
})