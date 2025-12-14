package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing flashcards locally
 */
@Entity(tableName = "flashcards")
@TypeConverters(Converters::class)
data class FlashcardEntity(
    @PrimaryKey
    val id: String,
    val frontText: String,
    val frontAudio: String?,
    val frontImage: String?,
    val frontTranslation: String?,
    val frontExplanation: String?,
    val backText: String,
    val backAudio: String?,
    val backImage: String?,
    val backTranslation: String?,
    val backExplanation: String?,
    val topic: VocabularyTopic,
    val level: JLPTLevel,
    val difficulty: Float,
    val lastReviewed: Instant?,
    val nextReview: Instant?,
    val isCustom: Boolean,
    val createdBy: String?,
    val lastModified: Instant,
    val needsSync: Boolean = false
) {
    fun toFlashcard(): Flashcard {
        return Flashcard(
            id = id,
            front = FlashcardSide(
                text = frontText,
                audio = frontAudio,
                image = frontImage,
                translation = frontTranslation,
                explanation = frontExplanation
            ),
            back = FlashcardSide(
                text = backText,
                audio = backAudio,
                image = backImage,
                translation = backTranslation,
                explanation = backExplanation
            ),
            topic = topic,
            level = level,
            difficulty = difficulty,
            lastReviewed = lastReviewed,
            nextReview = nextReview,
            isCustom = isCustom,
            createdBy = createdBy
        )
    }
    
    companion object {
        fun fromFlashcard(flashcard: Flashcard, needsSync: Boolean = false): FlashcardEntity {
            return FlashcardEntity(
                id = flashcard.id,
                frontText = flashcard.front.text,
                frontAudio = flashcard.front.audio,
                frontImage = flashcard.front.image,
                frontTranslation = flashcard.front.translation,
                frontExplanation = flashcard.front.explanation,
                backText = flashcard.back.text,
                backAudio = flashcard.back.audio,
                backImage = flashcard.back.image,
                backTranslation = flashcard.back.translation,
                backExplanation = flashcard.back.explanation,
                topic = flashcard.topic,
                level = flashcard.level,
                difficulty = flashcard.difficulty,
                lastReviewed = flashcard.lastReviewed,
                nextReview = flashcard.nextReview,
                isCustom = flashcard.isCustom,
                createdBy = flashcard.createdBy,
                lastModified = Instant.now(),
                needsSync = needsSync
            )
        }
    }
}