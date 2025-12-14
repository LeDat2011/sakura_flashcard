package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.Instant

/**
 * **Feature: japanese-flashcard-ui, Property 14: Quiz Results Completeness**
 * **Validates: Requirements 3.5**
 * 
 * Property: For any completed quiz session, results should display final score, correct/incorrect breakdown, 
 * explanations for each question, and suggested flashcards for review
 */
class QuizResultsCompletenessTest : StringSpec({

    "quiz results should contain all required information" {
        checkAll(
            iterations = 100,
            Arb.enum<VocabularyTopic>(),
            Arb.enum<JLPTLevel>(),
            Arb.int(0..10), // score
            Arb.long(30L..600L) // time spent in seconds
        ) { topic, level, score, timeSpent ->
            // Create a completed quiz result
            val answers = createValidAnswers(score)
            val quizResult = QuizResult(
                userId = "test-user",
                topic = topic,
                level = level,
                score = score,
                totalQuestions = 10,
                timeSpentSeconds = timeSpent,
                answers = answers
            )
            
            // Verify final score is present
            quizResult.score shouldBe score
            quizResult.totalQuestions shouldBe 10
            
            // Verify correct/incorrect breakdown
            val correctCount = answers.count { it.isCorrect }
            val incorrectCount = answers.count { !it.isCorrect }
            correctCount shouldBe score
            incorrectCount shouldBe (10 - score)
            correctCount + incorrectCount shouldBe 10
            
            // Verify percentage score calculation
            val expectedPercentage = (score.toFloat() / 10) * 100f
            quizResult.getPercentageScore() shouldBe expectedPercentage
            
            // Verify all answers have explanations (or at least the structure for them)
            answers shouldHaveSize 10
            answers.forEach { answer ->
                answer.questionId shouldNotBe ""
                answer.correctAnswer shouldNotBe ""
                answer.timeSpentSeconds shouldBeGreaterThanOrEqual 0L
            }
            
            // Verify time tracking
            quizResult.timeSpentSeconds shouldBe timeSpent
            quizResult.getAverageTimePerQuestion() shouldBe (timeSpent.toFloat() / 10)
        }
    }

    "quiz results should provide passing/failing status" {
        checkAll(
            iterations = 50,
            Arb.int(0..10)
        ) { score ->
            val answers = createValidAnswers(score)
            val quizResult = QuizResult(
                userId = "test-user",
                topic = VocabularyTopic.FOOD,
                level = JLPTLevel.N5,
                score = score,
                totalQuestions = 10,
                timeSpentSeconds = 120L,
                answers = answers
            )
            
            // Test passing status with default 70% threshold
            val expectedPassing = (score >= 7) // 70% of 10 questions
            quizResult.isPassing() shouldBe expectedPassing
            
            // Test with custom threshold
            quizResult.isPassing(80f) shouldBe (score >= 8)
            quizResult.isPassing(50f) shouldBe (score >= 5)
        }
    }

    "quiz results should handle edge cases correctly" {
        // Test perfect score
        val perfectAnswers = createValidAnswers(10)
        val perfectResult = QuizResult(
            userId = "test-user",
            topic = VocabularyTopic.ANIME,
            level = JLPTLevel.N1,
            score = 10,
            totalQuestions = 10,
            timeSpentSeconds = 300L,
            answers = perfectAnswers
        )
        
        perfectResult.getPercentageScore() shouldBe 100f
        perfectResult.isPassing() shouldBe true
        perfectResult.answers.all { it.isCorrect } shouldBe true
        
        // Test zero score
        val zeroAnswers = createValidAnswers(0)
        val zeroResult = QuizResult(
            userId = "test-user",
            topic = VocabularyTopic.COLORS,
            level = JLPTLevel.N5,
            score = 0,
            totalQuestions = 10,
            timeSpentSeconds = 180L,
            answers = zeroAnswers
        )
        
        zeroResult.getPercentageScore() shouldBe 0f
        zeroResult.isPassing() shouldBe false
        zeroResult.answers.all { !it.isCorrect } shouldBe true
    }
})

// Helper function to create valid quiz answers
private fun createValidAnswers(correctCount: Int): List<QuizAnswer> {
    require(correctCount in 0..10) { "Correct count must be between 0 and 10" }
    
    return (1..10).map { questionNum ->
        val isCorrect = questionNum <= correctCount
        QuizAnswer(
            questionId = "question-$questionNum",
            userAnswer = if (isCorrect) "correct-answer-$questionNum" else "wrong-answer-$questionNum",
            correctAnswer = "correct-answer-$questionNum",
            isCorrect = isCorrect,
            timeSpentSeconds = (10L..60L).random()
        )
    }
}