package com.example.sakura_flashcard.data.api

import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.data.model.FlashcardSide
import com.example.sakura_flashcard.data.model.GameResult
import com.example.sakura_flashcard.data.model.GameType
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.LearningProgress
import com.example.sakura_flashcard.data.model.Point
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection
import com.example.sakura_flashcard.data.model.User
import com.example.sakura_flashcard.data.model.VocabularyTopic
import com.example.sakura_flashcard.data.model.QuizAnswer as ModelQuizAnswer
import java.time.Instant
import java.util.UUID

// ==================== FlashcardDto → Flashcard ====================

fun FlashcardDto.toModel(): Flashcard {
    return Flashcard(
        id = id,
        front = FlashcardSide(
            text = front,
            audio = audioUrl,
            image = imageUrl,
            translation = null,
            explanation = null
        ),
        back = FlashcardSide(
            text = back,
            audio = null,
            image = null,
            translation = reading,
            explanation = null
        ),
        topic = topic?.toVocabularyTopic() ?: VocabularyTopic.DAILY_LIFE,
        level = level?.toJLPTLevel() ?: JLPTLevel.N5,
        difficulty = 1.0f,
        lastReviewed = null,
        nextReview = null,
        isCustom = false,
        createdBy = null
    )
}

fun List<FlashcardDto>.toFlashcardModels(): List<Flashcard> = map { it.toModel() }

// ==================== CharacterDto → Character ====================

fun CharacterDto.toModel(): Character {
    return Character(
        id = id,
        character = character,
        script = type.toCharacterScript(),
        pronunciation = listOf(romaji),
        strokeOrder = strokeOrder?.mapIndexed { index, path ->
            Stroke(
                order = index + 1,
                points = listOf(Point(0f, 0f)), // Default point
                direction = StrokeDirection.HORIZONTAL,
                path = path,
                imageUrl = null
            )
        } ?: emptyList(),
        examples = examples ?: emptyList()
    )
}

fun List<CharacterDto>.toCharacterModels(): List<Character> = map { it.toModel() }

// ==================== QuizResultDto → QuizResult ====================

fun QuizResultDto.toModel(userId: String = ""): QuizResult {
    return QuizResult(
        id = attempt.quizSetId + "_" + UUID.randomUUID().toString().take(8),
        userId = userId,
        topic = VocabularyTopic.DAILY_LIFE,
        level = JLPTLevel.N5,
        score = attempt.score,
        totalQuestions = attempt.totalQuestions,
        completedAt = attempt.completedAt.toInstantSafe(),
        timeSpentSeconds = 0L,
        answers = results.map { result ->
            ModelQuizAnswer(
                questionId = result.questionId,
                userAnswer = result.userAnswer ?: "",
                correctAnswer = result.correctAnswer ?: "",
                isCorrect = result.isCorrect,
                timeSpentSeconds = 0L
            )
        }
    )
}

fun List<QuizResultDto>.toQuizResultModels(userId: String = ""): List<QuizResult> =
    map { it.toModel(userId) }

// ==================== GameResultDto → GameResult ====================

fun GameResultDto.toModel(userId: String = ""): GameResult {
    return GameResult(
        id = id ?: UUID.randomUUID().toString(),
        userId = userId,
        gameType = gameType.toGameType(),
        score = score,
        completedAt = completedAt?.toInstantSafe() ?: Instant.now(),
        timeSpentSeconds = timeSpent.toLong(),
        level = JLPTLevel.N5
    )
}

fun List<GameResultDto>.toGameResultModels(userId: String = ""): List<GameResult> =
    map { it.toModel(userId) }

// ==================== UserProfile (API) → User (Model) ====================

fun UserProfile.toUser(): User {
    return User(
        id = id,
        username = username,
        email = email,
        avatar = profile.avatar,
        displayName = profile.displayName,
        age = null,
        currentLevel = profile.currentLevel.toJLPTLevel(),
        targetLevel = JLPTLevel.N2,
        dailyStudyGoalMinutes = 15,
        isOnboardingCompleted = true,
        createdAt = Instant.now(),
        lastLogin = stats?.lastStudyDate?.toInstantSafe(),
        learningProgress = LearningProgress(
            flashcardsLearned = 0,
            quizzesCompleted = 0,
            currentStreak = stats?.currentStreak ?: 0,
            totalStudyTimeMinutes = 0L,
            levelProgress = emptyMap()
        )
    )
}

// ==================== UserProgressDto → LearningProgress ====================

fun UserProgressDto.toLearningProgress(): LearningProgress {
    return LearningProgress(
        flashcardsLearned = vocabularyProgress.learned,
        quizzesCompleted = quizProgress.completed,
        currentStreak = currentStreak,
        totalStudyTimeMinutes = 0L,
        levelProgress = emptyMap()
    )
}

// ==================== VocabularyDto → Flashcard ====================

fun VocabularyDto.toFlashcard(): Flashcard {
    return Flashcard(
        id = id,
        front = FlashcardSide(
            text = word.japanese,
            audio = null,
            image = null,
            translation = null,
            explanation = null
        ),
        back = FlashcardSide(
            text = word.meaning,
            audio = null,
            image = null,
            translation = word.reading,
            explanation = details.examples.firstOrNull()?.let { "${it.japanese} - ${it.meaning}" }
        ),
        topic = topic.toVocabularyTopic(),
        level = level.toJLPTLevel(),
        difficulty = 1.0f,
        lastReviewed = null,
        nextReview = null,
        isCustom = false,
        createdBy = null
    )
}

fun List<VocabularyDto>.toFlashcards(): List<Flashcard> = map { it.toFlashcard() }

// ==================== Helper Extension Functions ====================

private fun String.toVocabularyTopic(): VocabularyTopic {
    return try {
        VocabularyTopic.valueOf(this.uppercase().replace(" ", "_"))
    } catch (e: Exception) {
        // Try to match by display name
        VocabularyTopic.values().find { 
            it.name.equals(this, ignoreCase = true) || 
            it.displayName.equals(this, ignoreCase = true) 
        } ?: VocabularyTopic.DAILY_LIFE
    }
}

private fun String.toJLPTLevel(): JLPTLevel {
    return try {
        JLPTLevel.valueOf(this.uppercase())
    } catch (e: Exception) {
        JLPTLevel.N5
    }
}

private fun String.toCharacterScript(): CharacterScript {
    return when (this.lowercase()) {
        "hiragana" -> CharacterScript.HIRAGANA
        "katakana" -> CharacterScript.KATAKANA
        "kanji" -> CharacterScript.KANJI
        else -> CharacterScript.HIRAGANA
    }
}

private fun String.toGameType(): GameType {
    return when (this.lowercase().replace("_", "").replace(" ", "")) {
        "sentenceorderpuzzle", "sentenceorder" -> GameType.SENTENCE_ORDER_PUZZLE
        "quickanswerchallenge", "quickanswer" -> GameType.QUICK_ANSWER_CHALLENGE
        "memorymatchgame", "memorymatch", "matching" -> GameType.MEMORY_MATCH_GAME
        else -> GameType.MEMORY_MATCH_GAME
    }
}

private fun String.toInstantSafe(): Instant {
    return try {
        Instant.parse(this)
    } catch (e: Exception) {
        try {
            java.time.LocalDateTime.parse(this)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
        } catch (e2: Exception) {
            Instant.now()
        }
    }
}

// ==================== Reverse Mappers (Model → DTO) ====================

fun Flashcard.toDto(): FlashcardDto {
    return FlashcardDto(
        id = id,
        front = front.text,
        back = back.text,
        reading = back.translation,
        topic = topic.name,
        level = level.name,
        category = null,
        audioUrl = front.audio,
        imageUrl = front.image
    )
}

fun QuizResult.toDto(): QuizResultDto {
    return QuizResultDto(
        attempt = QuizAttemptSummary(
            quizSetId = id,
            score = score,
            correctCount = answers.count { it.isCorrect },
            totalQuestions = totalQuestions,
            passed = score >= (totalQuestions * 0.7).toInt(),
            completedAt = completedAt.toString()
        ),
        results = answers.map { answer ->
            QuestionResult(
                questionId = answer.questionId,
                userAnswer = answer.userAnswer,
                correctAnswer = answer.correctAnswer,
                isCorrect = answer.isCorrect,
                points = if (answer.isCorrect) 1 else 0,
                explanation = null
            )
        },
        summary = QuizSummary(
            score = score,
            correctCount = answers.count { it.isCorrect },
            totalQuestions = totalQuestions,
            passed = score >= (totalQuestions * 0.7).toInt(),
            passingScore = (totalQuestions * 0.7).toInt()
        )
    )
}

fun GameResult.toDto(): GameResultDto {
    return GameResultDto(
        id = id,
        gameType = gameType.name,
        score = score,
        correctCount = score,
        totalQuestions = score,
        timeSpent = timeSpentSeconds.toInt(),
        completedAt = completedAt.toString()
    )
}
