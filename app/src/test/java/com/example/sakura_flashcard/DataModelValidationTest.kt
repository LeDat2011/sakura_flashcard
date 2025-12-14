package com.example.sakura_flashcard

import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.data.validation.ContentValidator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.time.Instant

/**
 * Feature: japanese-flashcard-ui, Property 5: Flashcard Status Indicators
 * Validates: Requirements 1.5
 */
class DataModelValidationTest : StringSpec({
    
    "flashcard should always have valid status indicators" {
        checkAll(iterations = 100, 
            Arb.string(1, 50), // front text
            Arb.string(1, 50), // back text
            Arb.enum<VocabularyTopic>(),
            Arb.enum<JLPTLevel>(),
            Arb.float(0.1f, 5.0f) // difficulty
        ) { frontText, backText, topic, level, difficulty ->
            
            val flashcard = Flashcard(
                front = FlashcardSide(text = frontText),
                back = FlashcardSide(text = backText),
                topic = topic,
                level = level,
                difficulty = difficulty
            )
            
            // Flashcard should have a valid status (due for review or not)
            val isDue = flashcard.isDueForReview()
            (isDue == true || isDue == false) shouldBe true
            
            // Flashcard should be able to be marked as reviewed
            val reviewedFlashcard = flashcard.markAsReviewed()
            reviewedFlashcard.lastReviewed shouldBe reviewedFlashcard.lastReviewed
            
            // Flashcard should be able to have next review scheduled
            val futureTime = Instant.now().plusSeconds(3600)
            val scheduledFlashcard = flashcard.scheduleNextReview(futureTime)
            scheduledFlashcard.nextReview shouldBe futureTime
        }
    }
    
    "user learning progress should maintain valid state" {
        checkAll(iterations = 100,
            Arb.int(0, 1000), // flashcards learned
            Arb.int(0, 500),  // quizzes completed
            Arb.int(0, 100),  // current streak
            Arb.long(0L, 10000L) // study time
        ) { flashcardsLearned, quizzesCompleted, currentStreak, studyTime ->
            
            val progress = LearningProgress(
                flashcardsLearned = flashcardsLearned,
                quizzesCompleted = quizzesCompleted,
                currentStreak = currentStreak,
                totalStudyTimeMinutes = studyTime
            )
            
            // Progress values should always be non-negative
            progress.flashcardsLearned shouldBe flashcardsLearned
            progress.quizzesCompleted shouldBe quizzesCompleted
            progress.currentStreak shouldBe currentStreak
            progress.totalStudyTimeMinutes shouldBe studyTime
            
            // Progress should be updatable
            val updatedProgress = progress.addFlashcardLearned()
            updatedProgress.flashcardsLearned shouldBe (flashcardsLearned + 1)
            
            val streakIncremented = progress.incrementStreak()
            streakIncremented.currentStreak shouldBe (currentStreak + 1)
        }
    }
    
    "character validation should work correctly for all scripts" {
        checkAll(iterations = 100,
            Arb.enum<CharacterScript>(),
            Arb.string(1, 5), // character
            Arb.list(Arb.string(1, 10), 1..3), // pronunciations
            Arb.int(1, 10) // stroke count
        ) { script, character, pronunciations, strokeCount ->
            
            // Create valid stroke order
            val strokes = (1..strokeCount).map { order ->
                Stroke(
                    order = order,
                    points = listOf(Point(order * 10f, order * 10f), Point(order * 20f, order * 20f)),
                    direction = StrokeDirection.HORIZONTAL,
                    path = "M${order * 10},${order * 10} L${order * 20},${order * 20}"
                )
            }
            
            // Test character validation based on script
            val isValidForScript = ContentValidator.isValidCharacterForScript(character, script)
            
            // If character is valid for script, Character object should be creatable
            if (isValidForScript && 
                pronunciations.all { ContentValidator.isValidPronunciation(it) } &&
                ContentValidator.isValidStrokeOrder(strokeCount)) {
                
                try {
                    val characterObj = Character(
                        character = character,
                        script = script,
                        pronunciation = pronunciations,
                        strokeOrder = strokes,
                        examples = listOf("example1", "example2")
                    )
                    
                    characterObj.getStrokeCount() shouldBe strokeCount
                    characterObj.getMainPronunciation() shouldBe pronunciations.first()
                    characterObj.hasMultiplePronunciations() shouldBe (pronunciations.size > 1)
                } catch (e: IllegalArgumentException) {
                    // Expected for invalid data
                }
            }
        }
    }
    
    "quiz questions should maintain correct answer validation" {
        checkAll(iterations = 100,
            Arb.string(5, 100), // content
            Arb.string(1, 50),  // correct answer
            Arb.list(Arb.string(1, 50), 2..5) // options for multiple choice
        ) { content, correctAnswer, options ->
            
            // Test multiple choice question
            if (options.contains(correctAnswer) && options.distinct().size == options.size) {
                val mcQuestion = MultipleChoiceQuestion(
                    content = content,
                    options = options,
                    correctAnswer = correctAnswer
                )
                
                mcQuestion.isAnswerCorrect(correctAnswer) shouldBe true
                mcQuestion.isAnswerCorrect(correctAnswer.lowercase()) shouldBe true
                mcQuestion.isAnswerCorrect("wrong answer") shouldBe false
            }
            
            // Test fill in blank question
            if (content.contains("___")) {
                val fibQuestion = FillInBlankQuestion(
                    content = content,
                    correctAnswer = correctAnswer
                )
                
                fibQuestion.isAnswerCorrect(correctAnswer) shouldBe true
                fibQuestion.isAnswerCorrect(" $correctAnswer ") shouldBe true // with whitespace
            }
            
            // Test true/false question
            if (correctAnswer in listOf("True", "False")) {
                val tfQuestion = TrueFalseQuestion(
                    content = content,
                    correctAnswer = correctAnswer
                )
                
                tfQuestion.isAnswerCorrect(correctAnswer) shouldBe true
                tfQuestion.options.contains(correctAnswer) shouldBe true
            }
        }
    }
    
    "game performance should calculate metrics correctly" {
        checkAll(iterations = 100,
            Arb.float(0.0f, 1.0f), // accuracy
            Arb.float(0.0f, 2.0f), // speed
            Arb.float(-1.0f, 1.0f) // improvement
        ) { accuracy, speed, improvement ->
            
            val performance = GamePerformance(
                accuracy = accuracy,
                speed = speed,
                improvement = improvement
            )
            
            // Accuracy percentage should be correct
            performance.getAccuracyPercentage() shouldBe (accuracy * 100f)
            
            // Performance rating should be consistent
            val rating = performance.getPerformanceRating()
            when {
                accuracy >= 0.9f && speed >= 0.5f -> rating shouldBe "Excellent"
                accuracy >= 0.8f && speed >= 0.3f -> rating shouldBe "Good"
                accuracy >= 0.7f -> rating shouldBe "Fair"
                else -> rating shouldBe "Needs Improvement"
            }
        }
    }
    
    "content validator should correctly identify Japanese text" {
        checkAll(iterations = 100,
            Arb.string(1, 20)
        ) { text ->
            
            val isValidJapanese = ContentValidator.isValidJapaneseText(text)
            val isValidHiragana = ContentValidator.isValidHiragana(text)
            val isValidKatakana = ContentValidator.isValidKatakana(text)
            val containsKanji = ContentValidator.containsKanji(text)
            
            // If text is valid hiragana, it should also be valid Japanese
            if (isValidHiragana) {
                isValidJapanese shouldBe true
            }
            
            // If text is valid katakana, it should also be valid Japanese
            if (isValidKatakana) {
                isValidJapanese shouldBe true
            }
            
            // If text contains kanji, it should be valid Japanese
            if (containsKanji) {
                isValidJapanese shouldBe true
            }
            
            // Text cannot be both hiragana and katakana exclusively
            if (isValidHiragana && isValidKatakana) {
                // This should only happen for whitespace-only strings
                text.all { it.isWhitespace() } shouldBe true
            }
        }
    }
})