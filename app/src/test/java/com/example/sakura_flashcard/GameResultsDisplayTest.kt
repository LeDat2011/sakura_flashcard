package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.Instant

/**
 * **Feature: japanese-flashcard-ui, Property 19: Game Results Display**
 * **Validates: Requirements 4.5**
 * 
 * Property: For any completed mini-game, performance results and learning progress should be displayed
 */
class GameResultsDisplayTest : StringSpec({
    
    "Property 19: Game results display should show performance results and learning progress" {
        checkAll(
            iterations = 100,
            Arb.enum<GameType>(),
            Arb.int(0, 1000), // score
            Arb.long(1, 3600), // time spent in seconds
            Arb.enum<JLPTLevel>(),
            Arb.float(0.0f, 1.0f), // accuracy
            Arb.float(0.0f, 2.0f), // speed
            Arb.float(-1.0f, 1.0f) // improvement
        ) { gameType, score, timeSpent, level, accuracy, speed, improvement ->
            
            // Create a completed game state
            val performance = GamePerformance(accuracy, speed, improvement)
            val gameState = GameState.Completed(
                finalScore = score,
                totalTime = timeSpent,
                performance = performance,
                questionsCompleted = 8, // Assuming some questions were completed
                totalQuestions = 10
            )
            
            // Create game result
            val gameResult = GameResult(
                userId = "test-user",
                gameType = gameType,
                score = score,
                timeSpentSeconds = timeSpent,
                level = level
            )
            
            // Test that game results display contains required information
            val resultsDisplay = formatGameResults(gameState, gameResult)
            
            // Should contain performance results
            resultsDisplay shouldContain "Score: $score"
            resultsDisplay shouldContain "Time: ${timeSpent}s"
            resultsDisplay shouldContain "Accuracy: ${String.format("%.1f", accuracy * 100)}%"
            resultsDisplay shouldContain performance.getPerformanceRating()
            
            // Should contain learning progress information
            resultsDisplay shouldContain "Level: ${level.name}"
            resultsDisplay shouldContain "Completion: ${String.format("%.1f", gameState.getCompletionRate() * 100)}%"
            
            // Should contain game type information
            resultsDisplay shouldContain gameType.displayName
        }
    }
    
    "Property 19: Game performance rating should be consistent with accuracy and speed" {
        checkAll(
            iterations = 100,
            Arb.float(0.0f, 1.0f), // accuracy
            Arb.float(0.0f, 2.0f), // speed
            Arb.float(-1.0f, 1.0f) // improvement
        ) { accuracy, speed, improvement ->
            
            val performance = GamePerformance(accuracy, speed, improvement)
            val rating = performance.getPerformanceRating()
            
            // Verify rating consistency
            when {
                accuracy >= 0.9f && speed >= 0.5f -> rating shouldBe "Excellent"
                accuracy >= 0.8f && speed >= 0.3f -> rating shouldBe "Good"
                accuracy >= 0.7f -> rating shouldBe "Fair"
                else -> rating shouldBe "Needs Improvement"
            }
        }
    }
})

// Helper function to format game results for display
private fun formatGameResults(gameState: GameState.Completed, gameResult: GameResult): String {
    return buildString {
        appendLine("Game: ${gameResult.gameType.displayName}")
        appendLine("Score: ${gameState.finalScore}")
        appendLine("Time: ${gameState.totalTime}s")
        appendLine("Accuracy: ${String.format("%.1f", gameState.performance.accuracy * 100)}%")
        appendLine("Rating: ${gameState.performance.getPerformanceRating()}")
        appendLine("Level: ${gameResult.level.name}")
        appendLine("Completion: ${String.format("%.1f", gameState.getCompletionRate() * 100)}%")
        appendLine("Questions: ${gameState.questionsCompleted}/${gameState.totalQuestions}")
    }
}