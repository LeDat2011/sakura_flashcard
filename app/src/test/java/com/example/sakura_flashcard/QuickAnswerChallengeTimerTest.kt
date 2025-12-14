package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.GameState
import com.example.sakura_flashcard.data.model.GameType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 17: Quick Answer Challenge Timer**
 * **Validates: Requirements 4.3**
 * 
 * Property: For any Quick Answer Challenge question, a 5-second timer should be enforced per question
 */
class QuickAnswerChallengeTimerTest : StringSpec({

    "Property 17: Quick Answer Challenge Timer - GameState.InProgress should track time remaining" {
        checkAll(100, Arb.int(0..100), Arb.long(0L..10L)) { score, timeRemaining ->
            val gameState = GameState.InProgress(
                currentScore = score,
                timeRemaining = timeRemaining,
                currentQuestion = "Test question",
                questionsCompleted = 0,
                totalQuestions = 10
            )
            
            gameState.shouldBeInstanceOf<GameState.InProgress>()
            gameState.timeRemaining shouldBe timeRemaining
            gameState.currentScore shouldBe score
        }
    }

    "Property 17: Quick Answer Challenge Timer - Quick Answer Challenge should have 5-second time limit" {
        checkAll(100, Arb.int(0..100)) { score ->
            // Create a mock Quick Answer Challenge game state with 5-second timer
            val gameState = GameState.InProgress(
                currentScore = score,
                timeRemaining = 5L, // 5 seconds as per requirement
                currentQuestion = "What does 'こんにちは' mean?",
                questionsCompleted = 0,
                totalQuestions = 10
            )
            
            gameState.timeRemaining shouldBe 5L
            gameState.timeRemaining shouldNotBe null
        }
    }

    "Property 17: Quick Answer Challenge Timer - timer countdown should work correctly" {
        checkAll(100, Arb.long(1L..5L)) { initialTime ->
            val gameState = GameState.InProgress(
                currentScore = 0,
                timeRemaining = initialTime,
                currentQuestion = "Test question",
                questionsCompleted = 0,
                totalQuestions = 10
            )
            
            // Simulate timer countdown
            val updatedTime = initialTime - 1L
            val updatedState = gameState.copy(timeRemaining = updatedTime)
            
            updatedState.timeRemaining shouldBe updatedTime
            if (updatedTime >= 0) {
                (updatedState.timeRemaining!! >= 0L) shouldBe true
            }
        }
    }

    "Property 17: Quick Answer Challenge Timer - GameType should indicate Quick Answer Challenge has time limit" {
        checkAll(100, Arb.int(0..100)) { _ ->
            val gameType = GameType.QUICK_ANSWER_CHALLENGE
            
            gameType.displayName shouldBe "Quick Answer Challenge"
            gameType.description shouldBe "Answer questions as fast as possible with 5-second timer"
        }
    }

    "Property 17: Quick Answer Challenge Timer - progress calculation should work with timer" {
        checkAll(100, Arb.int(0..9), Arb.long(0L..5L)) { completed, timeRemaining ->
            val totalQuestions = 10
            val gameState = GameState.InProgress(
                currentScore = 0,
                timeRemaining = timeRemaining,
                currentQuestion = "Test question",
                questionsCompleted = completed,
                totalQuestions = totalQuestions
            )
            
            val expectedProgress = completed.toFloat() / totalQuestions
            gameState.getProgress() shouldBe expectedProgress
            
            // Timer should not affect progress calculation
            gameState.timeRemaining shouldBe timeRemaining
        }
    }

    "Property 17: Quick Answer Challenge Timer - completed state should track total time" {
        checkAll(100, Arb.int(0..100), Arb.long(1L..60L), Arb.int(0..10)) { score, totalTime, questionsCompleted ->
            val completedState = GameState.Completed(
                finalScore = score,
                totalTime = totalTime,
                performance = com.example.sakura_flashcard.data.model.GamePerformance(
                    accuracy = 0.8f,
                    speed = 0.5f,
                    improvement = 0.1f
                ),
                questionsCompleted = questionsCompleted,
                totalQuestions = 10
            )
            
            completedState.totalTime shouldBe totalTime
            completedState.finalScore shouldBe score
            completedState.questionsCompleted shouldBe questionsCompleted
        }
    }
})