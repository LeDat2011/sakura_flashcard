package com.example.sakura_flashcard.ui.accessibility

import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Service for handling accessibility features including text-to-speech for Japanese pronunciation
 */
class AccessibilityService(private val context: Context) {
    
    private var textToSpeech: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    private val _isJapaneseSupported = MutableStateFlow(false)
    val isJapaneseSupported: StateFlow<Boolean> = _isJapaneseSupported.asStateFlow()
    
    init {
        initializeTextToSpeech()
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                _isInitialized.value = true
                
                // Check if Japanese is supported
                val japaneseResult = textToSpeech?.setLanguage(Locale.JAPANESE)
                _isJapaneseSupported.value = when (japaneseResult) {
                    TextToSpeech.LANG_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> true
                    else -> false
                }
                
                // Set default language back to system default
                textToSpeech?.setLanguage(Locale.getDefault())
            }
        }
    }
    
    /**
     * Speaks Japanese text with proper pronunciation
     */
    fun speakJapanese(
        text: String,
        utteranceId: String = "japanese_${System.currentTimeMillis()}"
    ) {
        textToSpeech?.let { tts ->
            // Try Japanese first, fallback to default if not supported
            val result = tts.setLanguage(Locale.JAPANESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default language
                tts.setLanguage(Locale.getDefault())
            }
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }
    
    /**
     * Speaks English text (for translations and explanations)
     */
    fun speakEnglish(
        text: String,
        utteranceId: String = "english_${System.currentTimeMillis()}"
    ) {
        textToSpeech?.let { tts ->
            tts.setLanguage(Locale.ENGLISH)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            // Reset to default language after speaking
            tts.setLanguage(Locale.getDefault())
        }
    }
    
    /**
     * Speaks text in the system default language
     */
    fun speak(
        text: String,
        utteranceId: String = "default_${System.currentTimeMillis()}"
    ) {
        textToSpeech?.let { tts ->
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }
    
    /**
     * Stops current speech
     */
    fun stop() {
        textToSpeech?.stop()
    }
    
    /**
     * Checks if TTS is currently speaking
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    /**
     * Sets speech rate (0.5 = half speed, 1.0 = normal, 2.0 = double speed)
     */
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate.coerceIn(0.1f, 3.0f))
    }
    
    /**
     * Sets speech pitch (0.5 = lower pitch, 1.0 = normal, 2.0 = higher pitch)
     */
    fun setSpeechPitch(pitch: Float) {
        textToSpeech?.setPitch(pitch.coerceIn(0.1f, 2.0f))
    }
    
    /**
     * Announces accessibility information for screen readers
     */
    fun announceForAccessibility(text: String) {
        // This would typically use AccessibilityManager to announce
        // For now, we'll use TTS as a fallback
        speak(text)
    }
    
    /**
     * Creates comprehensive audio description for flashcard content
     */
    /**
     * Speaks flashcard content - only Japanese text directly without introduction
     */
    fun speakFlashcardContent(
        japaneseText: String,
        pronunciation: String? = null,
        translation: String? = null,
        explanation: String? = null,
        isBack: Boolean = false
    ) {
        // Only speak the Japanese text directly without introduction
        if (japaneseText.isNotBlank()) {
            speakJapanese(japaneseText)
        }
    }
    
    /**
     * Releases TTS resources
     */
    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        _isInitialized.value = false
        _isJapaneseSupported.value = false
    }
}

/**
 * Composable to provide accessibility service
 */
@Composable
fun rememberAccessibilityService(): AccessibilityService {
    val context = LocalContext.current
    
    return remember(context) {
        AccessibilityService(context)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            // Note: We don't shutdown here as the service might be used elsewhere
            // Shutdown should be handled at the application level
        }
    }
}

/**
 * Accessibility preferences for speech settings
 */
data class SpeechPreferences(
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val enableJapaneseTTS: Boolean = true,
    val enableAutoSpeak: Boolean = false,
    val announceFlashcardSides: Boolean = true
)