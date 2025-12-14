package com.example.sakura_flashcard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.sakura_flashcard.data.validation.ContentValidator
import java.time.Instant
import java.util.UUID

@Entity(tableName = "flashcards")
@TypeConverters(Converters::class)
data class Flashcard(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val front: FlashcardSide,
    val back: FlashcardSide,
    val topic: VocabularyTopic,
    val level: JLPTLevel,
    val difficulty: Float = 1.0f,
    val lastReviewed: Instant? = null,
    val nextReview: Instant? = null,
    val isCustom: Boolean = false,
    val createdBy: String? = null
) {
    init {
        require(ContentValidator.isValidFlashcardText(front.text)) { 
            "Front text cannot be empty and must be less than 500 characters" 
        }
        require(ContentValidator.isValidFlashcardText(back.text)) { 
            "Back text cannot be empty and must be less than 500 characters" 
        }
        require(ContentValidator.isValidDifficulty(difficulty)) { 
            "Difficulty must be between 0.1 and 5.0" 
        }
    }
    
    fun markAsReviewed(): Flashcard {
        return copy(lastReviewed = Instant.now())
    }
    
    fun scheduleNextReview(nextReviewTime: Instant): Flashcard {
        return copy(nextReview = nextReviewTime)
    }
    
    fun updateDifficulty(newDifficulty: Float): Flashcard {
        require(ContentValidator.isValidDifficulty(newDifficulty)) { 
            "Difficulty must be between 0.1 and 5.0" 
        }
        return copy(difficulty = newDifficulty)
    }
    
    fun isDueForReview(): Boolean {
        return nextReview?.let { it.isBefore(Instant.now()) } ?: true
    }
}

data class FlashcardSide(
    val text: String,
    val audio: String? = null,
    val image: String? = null,
    val translation: String? = null,
    val explanation: String? = null,
    val hiragana: String? = null,
    val romaji: String? = null,
    val wordType: String? = null,
    val exampleSentence: String? = null,
    val exampleSentenceMeaning: String? = null
) {
    init {
        require(text.isNotBlank()) { "Text cannot be empty" }
        require(text.length <= 500) { "Text must be less than 500 characters" }
    }
    
    fun hasAudio(): Boolean = !audio.isNullOrBlank()
    fun hasImage(): Boolean = !image.isNullOrBlank()
    fun hasTranslation(): Boolean = !translation.isNullOrBlank()
    fun hasExplanation(): Boolean = !explanation.isNullOrBlank()
}

enum class VocabularyTopic(val displayName: String) {
    ANIME("Anime"),
    BODY_PARTS("Bộ phận cơ thể"),
    FOOD("Đồ ăn"),
    DAILY_LIFE("Cuộc sống hằng ngày"),
    ANIMALS("Động vật"),
    SCHOOL("Trường học"),
    TRAVEL("Du lịch"),
    WEATHER("Thời tiết"),
    FAMILY("Gia đình"),
    TECHNOLOGY("Công nghệ"),
    CLOTHES("Quần áo"),
    COLORS("Màu sắc"),
    NUMBERS("Số đếm"),
    COMMON_EXPRESSIONS("Câu thường dùng");
    
    companion object {
        fun fromDisplayName(displayName: String): VocabularyTopic? {
            return values().find { it.displayName == displayName }
        }
        
        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

enum class JLPTLevel(val displayName: String, val description: String) {
    N5("N5", "Sơ cấp - Từ vựng và ngữ pháp cơ bản"),
    N4("N4", "Cơ bản - Từ vựng và ngữ pháp mở rộng"),
    N3("N3", "Trung cấp - Ngữ pháp và từ vựng phức tạp hơn"),
    N2("N2", "Trung cao cấp - Ngữ pháp và từ vựng nâng cao"),
    N1("N1", "Cao cấp - Ngữ pháp và từ vựng bản ngữ");
    
    companion object {
        fun fromDisplayName(displayName: String): JLPTLevel? {
            return values().find { it.displayName == displayName }
        }
        
        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
    
    fun isEasierThan(other: JLPTLevel): Boolean {
        return this.ordinal < other.ordinal
    }
    
    fun isHarderThan(other: JLPTLevel): Boolean {
        return this.ordinal > other.ordinal
    }
}