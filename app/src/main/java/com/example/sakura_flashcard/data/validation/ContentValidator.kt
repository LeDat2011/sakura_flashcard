package com.example.sakura_flashcard.data.validation

import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.VocabularyTopic

object ContentValidator {
    
    // Japanese character ranges
    private val HIRAGANA_RANGE = '\u3040'..'\u309F'
    private val KATAKANA_RANGE = '\u30A0'..'\u30FF'
    private val KANJI_RANGE = '\u4E00'..'\u9FAF'
    private val JAPANESE_PUNCTUATION = '\u3000'..'\u303F'
    
    /**
     * Validates if text contains Japanese characters
     */
    fun isValidJapaneseText(text: String): Boolean {
        if (text.isBlank()) return false
        
        return text.any { char ->
            char in HIRAGANA_RANGE || 
            char in KATAKANA_RANGE || 
            char in KANJI_RANGE ||
            char in JAPANESE_PUNCTUATION ||
            char.isWhitespace()
        }
    }
    
    /**
     * Validates if text contains only Hiragana characters
     */
    fun isValidHiragana(text: String): Boolean {
        if (text.isBlank()) return false
        return text.all { char -> 
            char in HIRAGANA_RANGE || char.isWhitespace() 
        }
    }
    
    /**
     * Validates if text contains only Katakana characters
     */
    fun isValidKatakana(text: String): Boolean {
        if (text.isBlank()) return false
        return text.all { char -> 
            char in KATAKANA_RANGE || char.isWhitespace() 
        }
    }
    
    /**
     * Validates if text contains Kanji characters
     */
    fun containsKanji(text: String): Boolean {
        return text.any { char -> char in KANJI_RANGE }
    }
    
    /**
     * Validates character based on its script type
     */
    fun isValidCharacterForScript(character: String, script: CharacterScript): Boolean {
        return when (script) {
            CharacterScript.HIRAGANA -> isValidHiragana(character)
            CharacterScript.KATAKANA -> isValidKatakana(character)
            CharacterScript.KANJI -> containsKanji(character)
        }
    }
    
    /**
     * Validates email format
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
    
    /**
     * Validates username format
     */
    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank() && 
               username.length >= 3 && 
               username.length <= 20 &&
               username.matches("^[a-zA-Z0-9_]+$".toRegex())
    }
    
    /**
     * Validates flashcard text content
     */
    fun isValidFlashcardText(text: String): Boolean {
        return text.isNotBlank() && text.length <= 500
    }
    
    /**
     * Validates JLPT level progression (N5 is easiest, N1 is hardest)
     */
    fun isValidJLPTProgression(fromLevel: JLPTLevel, toLevel: JLPTLevel): Boolean {
        return fromLevel.ordinal >= toLevel.ordinal
    }
    
    /**
     * Validates difficulty score (should be between 0.1 and 5.0)
     */
    fun isValidDifficulty(difficulty: Float): Boolean {
        return difficulty in 0.1f..5.0f
    }
    
    /**
     * Validates vocabulary topic exists
     */
    fun isValidVocabularyTopic(topic: String): Boolean {
        return try {
            VocabularyTopic.valueOf(topic.uppercase())
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
    
    /**
     * Validates pronunciation format (romaji or hiragana)
     */
    fun isValidPronunciation(pronunciation: String): Boolean {
        if (pronunciation.isBlank()) return false
        
        // Allow romaji (latin characters) or hiragana
        val romajiRegex = "^[a-zA-Z\\s]+$".toRegex()
        return pronunciation.matches(romajiRegex) || isValidHiragana(pronunciation)
    }
    
    /**
     * Validates stroke order data
     */
    fun isValidStrokeOrder(strokeCount: Int): Boolean {
        return strokeCount in 1..30 // Most complex kanji have around 20-25 strokes
    }
}