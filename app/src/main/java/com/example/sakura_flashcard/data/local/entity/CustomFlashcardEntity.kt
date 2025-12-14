package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.Converters
import java.time.Instant

/**
 * Room entity for storing custom flashcards locally
 */
@Entity(
    tableName = "custom_flashcards",
    foreignKeys = [
        ForeignKey(
            entity = CustomDeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId")]
)
@TypeConverters(Converters::class)
data class CustomFlashcardEntity(
    @PrimaryKey
    val id: String,
    val deckId: String,
    val japanese: String,
    val romaji: String,
    val hiragana: String? = null,
    val audioUrl: String? = null,
    val imageUrl: String? = null,
    val vietnamese: String,
    val alternativeMeanings: List<String> = emptyList(),
    val wordType: String? = null,
    val explanation: String? = null,
    val exampleSentence: String? = null,
    val exampleSentenceMeaning: String? = null,
    val personalNote: String? = null,
    val isFlagged: Boolean = false,
    val order: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now()
)
