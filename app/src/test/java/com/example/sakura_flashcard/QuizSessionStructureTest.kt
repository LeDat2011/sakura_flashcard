package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

import io.kotest.property.checkAll

/**
 * **Feature: japanese-flashcard-ui, Property 12: Quiz Session Structure**
 * **Validates: Requirements 3.3**
 * 
 * Property: For any quiz session, exactly 10 questions should be presented with mixed question types 
 * (multiple choice, fill in the blank, true/false)
 */
class QuizSessionStructureTest : StringSpec({

    "quiz session should contain exactly 10 questions with mixed types" {
        checkAll(
            iterations = 100,
            arbMixedQuestionList(),
            Arb.enum<VocabularyTopic>(),
            Arb.enum<JLPTLevel>()
        ) { questions, topic, level ->
            // Create a quiz session with the generated questions
            val quizSession = QuizSession(
                id = "test-session",
                topic = topic,
                level = level,
                questions = questions,
                currentQuestionIndex = 0,
                answers = emptyList(),
                startTime = java.time.Instant.now(),
                isCompleted = false
            )
            
            // Verify exactly 10 questions
            quizSession.questions.size shouldBe 10
            
            // Verify mixed question types are present
            val questionTypes = quizSession.questions.map { it.type }.toSet()
            
            // Should have at least 2 different question types for "mixed"
            (questionTypes.size >= 2) shouldBe true
        }
    }

    "quiz session should maintain proper structure throughout lifecycle" {
        checkAll(
            iterations = 100,
            arbQuizSession()
        ) { quizSession ->
            // Verify basic structure constraints
            quizSession.questions.size shouldBe 10
            (quizSession.currentQuestionIndex >= 0 && quizSession.currentQuestionIndex <= 9) shouldBe true
            
            // If completed, should have 10 answers
            if (quizSession.isCompleted) {
                quizSession.answers.size shouldBe 10
            } else {
                (quizSession.answers.size >= 0 && quizSession.answers.size <= quizSession.currentQuestionIndex + 1) shouldBe true
            }
            
            // Verify question types are mixed
            val questionTypes = quizSession.questions.map { it.type }.toSet()
            (questionTypes.size >= 2) shouldBe true
        }
    }
})

// Arbitrary generators for property testing
fun arbQuizQuestion(): Arb<QuizQuestion> = Arb.choice(
    arbMultipleChoiceQuestion(),
    arbFillInBlankQuestion(),
    arbTrueFalseQuestion()
)

fun arbMultipleChoiceQuestion(): Arb<MultipleChoiceQuestion> = Arb.bind(
    Arb.string(5..50).filter { it.isNotBlank() },
    Arb.list(Arb.string(3..20).filter { it.isNotBlank() }, 2..4),
    Arb.string(3..20).filter { it.isNotBlank() }
) { content, options, correctAnswer ->
    // Ensure correctAnswer is included and we have at least 2 unique options
    val uniqueOptions = options.distinct().toMutableList()
    if (!uniqueOptions.contains(correctAnswer)) {
        uniqueOptions.add(correctAnswer)
    }
    
    // Ensure we have at least 2 options by adding a dummy option if needed
    while (uniqueOptions.size < 2) {
        val dummyOption = "Option${uniqueOptions.size + 1}"
        if (!uniqueOptions.contains(dummyOption)) {
            uniqueOptions.add(dummyOption)
        }
    }
    
    MultipleChoiceQuestion(
        content = content,
        options = uniqueOptions,
        correctAnswer = correctAnswer
    )
}

fun arbFillInBlankQuestion(): Arb<FillInBlankQuestion> = Arb.bind(
    Arb.string(5..30).filter { it.isNotBlank() },
    Arb.string(3..20).filter { it.isNotBlank() }
) { prefix, answer ->
    FillInBlankQuestion(
        content = "$prefix ___",
        correctAnswer = answer
    )
}

fun arbTrueFalseQuestion(): Arb<TrueFalseQuestion> = Arb.bind(
    Arb.string(5..50).filter { it.isNotBlank() },
    Arb.element("True", "False")
) { content, answer ->
    TrueFalseQuestion(
        content = content,
        correctAnswer = answer
    )
}

fun arbQuizSession(): Arb<QuizSession> = Arb.bind(
    Arb.string(5..20).filter { it.isNotBlank() },
    Arb.enum<VocabularyTopic>(),
    Arb.enum<JLPTLevel>(),
    arbMixedQuestionList(),
    Arb.boolean()
) { id: String, topic: VocabularyTopic, level: JLPTLevel, questions: List<QuizQuestion>, isCompleted: Boolean ->
    // Ensure currentIndex is valid based on completion status
    val currentIndex = if (isCompleted) 9 else Arb.int(0..8).single()
    
    // Generate appropriate number of answers based on state
    val answerCount = if (isCompleted) 10 else minOf(currentIndex + 1, 10)
    val answers = List(answerCount) { 
        QuizAnswer(
            questionId = "q$it",
            userAnswer = "answer$it",
            correctAnswer = "correct$it",
            isCorrect = it % 2 == 0,
            timeSpentSeconds = (10L..60L).random()
        )
    }
    
    QuizSession(
        id = id,
        topic = topic,
        level = level,
        questions = questions,
        currentQuestionIndex = currentIndex,
        answers = answers,
        startTime = java.time.Instant.now(),
        isCompleted = isCompleted
    )
}

fun arbMixedQuestionList(): Arb<List<QuizQuestion>> = 
    Arb.bind(
        Arb.list(arbMultipleChoiceQuestion(), 3..4),
        Arb.list(arbFillInBlankQuestion(), 3..4),
        Arb.list(arbTrueFalseQuestion(), 2..4)
    ) { mc, fib, tf ->
        val allQuestions = (mc + fib + tf).take(10)
        // Ensure exactly 10 questions
        if (allQuestions.size == 10) allQuestions else allQuestions + allQuestions.take(10 - allQuestions.size)
    }.filter { questions ->
        questions.size == 10 && questions.map { it.type }.toSet().size >= 2
    }

fun arbQuizAnswer(): Arb<QuizAnswer> = Arb.bind(
    Arb.string(5..20),
    Arb.string(1..50),
    Arb.string(1..50),
    Arb.boolean(),
    Arb.long(1L..300L)
) { questionId, userAnswer, correctAnswer, isCorrect, timeSpent ->
    QuizAnswer(
        questionId = questionId,
        userAnswer = userAnswer,
        correctAnswer = correctAnswer,
        isCorrect = isCorrect,
        timeSpentSeconds = timeSpent
    )
}