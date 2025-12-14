package com.example.sakura_flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.model.*
import java.time.Instant

/**
 * Room entity for storing character data locally
 */
@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class CharacterEntity(
    @PrimaryKey
    val id: String,
    val character: String,
    val script: CharacterScript,
    val pronunciation: List<String>,
    val strokeOrder: List<Stroke>,
    val examples: List<String>,
    val lastModified: Instant
) {
    fun toCharacter(): Character {
        return Character(
            id = id,
            character = character,
            script = script,
            pronunciation = pronunciation,
            strokeOrder = strokeOrder,
            examples = examples
        )
    }
    
    companion object {
        fun fromCharacter(character: Character): CharacterEntity {
            return CharacterEntity(
                id = character.id,
                character = character.character,
                script = character.script,
                pronunciation = character.pronunciation,
                strokeOrder = character.strokeOrder,
                examples = character.examples,
                lastModified = Instant.now()
            )
        }
    }
}