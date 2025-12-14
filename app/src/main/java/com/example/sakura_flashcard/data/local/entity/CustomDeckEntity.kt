package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.Converters
import java.time.Instant

/**
 * Room entity for storing custom decks locally
 */
@Entity(tableName = "custom_decks")
@TypeConverters(Converters::class)
data class CustomDeckEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val coverImage: String? = null,
    val color: String? = null,
    val icon: String? = null,
    val topic: String? = null,
    val level: String? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
    val userId: String = "default_user"
)
