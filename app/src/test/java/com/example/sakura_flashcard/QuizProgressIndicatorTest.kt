package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 13: Quiz Progress Indicator Format**
 * **Validates: Requirements 3.4**
 * 
 * Property: For any quiz question display, a progress indicator should show current question number 
 * and total in "current/total" format
 */
class QuizProgressIndicatorTest : StringSpec({

    "quiz progress indicator should display current/total format" {
        checkAll(
            iterations = 100,
            Arb.int(0..9), // current question index (0-based)
            Arb.int(10..10) // total questions (always 10 for quiz)
        ) { currentIndex, totalQuestions ->
            // Create a simple quiz session to test progress indicator
            val questions = List(totalQuestions) { 
                createValidMultipleChoiceQuestion("Question ${it + 1}")
            }
            
            val quizSession = createValidQuizSession(
                questions = questions,
                currentQuestionIndex = currentIndex
            )
            
            // Test the progress indicator format
            val progress = quizSession.getProgress()
            
            // Should match "current/total" format where current is 1-based
            val expectedCurrent = currentIndex + 1
            val expectedFormat = "$expectedCurrent/$totalQuestions"
            
            progress shouldBe expectedFormat
            progress shouldMatch Regex("\\d+/\\d+")
        }
    }

    "quiz progress indicator should handle edge cases correctly" {
        checkAll(
            iterations = 50,
            Arb.int(0..9)
        ) { currentIndex ->
            val questions = List(10) { 
                createValidMultipleChoiceQuestion("Question ${it + 1}")
            }
            
            val quizSession = createValidQuizSession(
                questions = questions,
                currentQuestionIndex = currentIndex
            )
            
            val progress = quizSession.getProgress()
            val parts = progress.split("/")
            
            // Should always have exactly 2 parts
            parts.size shouldBe 2
            
            // Current should be 1-based (1-10)
            val current = parts[0].toInt()
            current shouldBe (currentIndex + 1)
            (current >= 1 && current <= 10) shouldBe true
            
            // Total should always be 10
            val total = parts[1].toInt()
            total shouldBe 10
        }
    }
})

// Helper functions to create valid test data
private fun createValidMultipleChoiceQuestion(content: String): MultipleChoiceQuestion {
    return MultipleChoiceQuestion(
        content = content,
        options = listOf("Option A", "Option B", "Option C", "Option D"),
        correctAnswer = "Option A"
    )
}

private fun createValidQuizSession(
    questions: List<QuizQuestion>,
    currentQuestionIndex: Int = 0
): QuizSession {
    // Ensure we have mixed question types by replacing some questions
    val mixedQuestions = questions.toMutableList()
    if (mixedQuestions.size >= 3) {
        mixedQuestions[1] = FillInBlankQuestion(
            content = "Fill in the blank: Hello ___",
            correctAnswer = "World"
        )
        mixedQuestions[2] = TrueFalseQuestion(
            content = "This is a true/false question",
            correctAnswer = "True"
        )
    }
    
    return QuizSession(
        id = "test-session",
        topic = VocabularyTopic.FOOD,
        level = JLPTLevel.N5,
        questions = mixedQuestions,
        currentQuestionIndex = currentQuestionIndex,
        answers = emptyList(),
        startTime = java.time.Instant.now(),
        isCompleted = false
    )
}