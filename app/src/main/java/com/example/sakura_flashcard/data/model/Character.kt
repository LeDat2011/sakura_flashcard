package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.validation.ContentValidator
import java.util.UUID

@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class Character(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val character: String,
    val script: CharacterScript,
    val pronunciation: List<String>,
    val strokeOrder: List<Stroke>,
    val examples: List<String>
) {
    init {
        require(character.isNotBlank()) { "Character cannot be empty" }
        require(ContentValidator.isValidCharacterForScript(character, script)) { 
            "Character '$character' is not valid for script $script" 
        }
        require(pronunciation.isNotEmpty()) { "Character must have at least one pronunciation" }
        require(pronunciation.all { ContentValidator.isValidPronunciation(it) }) { 
            "All pronunciations must be valid" 
        }
        require(strokeOrder.isNotEmpty()) { "Character must have stroke order data" }
        require(ContentValidator.isValidStrokeOrder(strokeOrder.size)) { 
            "Stroke count must be between 1 and 30" 
        }
        require(strokeOrder.mapIndexed { index, stroke -> stroke.order == index + 1 }.all { it }) { 
            "Stroke order must be sequential starting from 1" 
        }
    }
    
    fun getMainPronunciation(): String {
        return pronunciation.first()
    }
    
    fun hasMultiplePronunciations(): Boolean {
        return pronunciation.size > 1
    }
    
    fun getStrokeCount(): Int {
        return strokeOrder.size
    }
    
    fun hasExamples(): Boolean {
        return examples.isNotEmpty()
    }
}

/**
 * Represents a stroke in character writing
 */
data class Stroke(
    val order: Int,
    val points: List<Point>,
    val direction: StrokeDirection,
    val path: String = "", // SVG path data for stroke (optional)
    val imageUrl: String? = null // URL for stroke animation image (optional)
) {
    init {
        require(order > 0) { "Stroke order must be positive" }
        require(points.isNotEmpty()) { "Stroke must have at least one point" }
    }
}

/**
 * Represents a point in 2D space for stroke drawing
 */
data class Point(
    val x: Float,
    val y: Float
)

enum class CharacterScript(val displayName: String, val description: String) {
    HIRAGANA("Hiragana", "Chữ cái Nhật cơ bản cho từ gốc Nhật"),
    KATAKANA("Katakana", "Chữ cái Nhật cho từ ngoại lai"),
    KANJI("Kanji", "Chữ Hán sử dụng trong tiếng Nhật");
    
    companion object {
        fun fromDisplayName(displayName: String): CharacterScript? {
            return values().find { it.displayName == displayName }
        }
        
        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

/**
 * Direction of a stroke
 */
enum class StrokeDirection(val displayName: String) {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical"),
    DIAGONAL_DOWN_RIGHT("Diagonal Down Right"),
    DIAGONAL_DOWN_LEFT("Diagonal Down Left"),
    CURVED("Curved"),
    DOT("Dot");
    
    companion object {
        fun fromDisplayName(displayName: String): StrokeDirection? {
            return values().find { it.displayName == displayName }
        }
    }
}