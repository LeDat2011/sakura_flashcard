package com.example.sakura_flashcard.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore for theme preferences
val android.content.Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

// Preference keys
object ThemePreferenceKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
    val COLOR_THEME = stringPreferencesKey("color_theme")
    val ENABLE_TTS = booleanPreferencesKey("enable_tts")
    val TTS_SPEECH_RATE = floatPreferencesKey("tts_speech_rate")
    val TTS_SPEECH_PITCH = floatPreferencesKey("tts_speech_pitch")
    val AUTO_SPEAK_FLASHCARDS = booleanPreferencesKey("auto_speak_flashcards")
    val ANNOUNCE_FLASHCARD_SIDES = booleanPreferencesKey("announce_flashcard_sides")
    val KEYBOARD_NAVIGATION = booleanPreferencesKey("keyboard_navigation")
    val FOCUS_INDICATORS = booleanPreferencesKey("focus_indicators")
}

// Theme configuration data class
data class ThemeConfig(
    val isDarkTheme: Boolean = false,
    val useDynamicColor: Boolean = true,
    val useHighContrast: Boolean = false,
    val fontScale: Float = 1.0f,
    val reduceMotion: Boolean = false,
    val colorTheme: String = "default",
    val enableTTS: Boolean = true,
    val ttsSpeechRate: Float = 1.0f,
    val ttsSpeechPitch: Float = 1.0f,
    val autoSpeakFlashcards: Boolean = false,
    val announceFlashcardSides: Boolean = true,
    val keyboardNavigation: Boolean = true,
    val focusIndicators: Boolean = true
)

// Theme preference repository
class ThemePreferencesRepository(private val dataStore: DataStore<Preferences>) {
    
    val themeConfig: Flow<ThemeConfig> = dataStore.data.map { preferences ->
        ThemeConfig(
            isDarkTheme = preferences[ThemePreferenceKeys.DARK_THEME] ?: false,
            useDynamicColor = preferences[ThemePreferenceKeys.DYNAMIC_COLOR] ?: true,
            useHighContrast = preferences[ThemePreferenceKeys.HIGH_CONTRAST] ?: false,
            fontScale = preferences[ThemePreferenceKeys.FONT_SCALE] ?: 1.0f,
            reduceMotion = preferences[ThemePreferenceKeys.REDUCE_MOTION] ?: false,
            colorTheme = preferences[ThemePreferenceKeys.COLOR_THEME] ?: "default",
            enableTTS = preferences[ThemePreferenceKeys.ENABLE_TTS] ?: true,
            ttsSpeechRate = preferences[ThemePreferenceKeys.TTS_SPEECH_RATE] ?: 1.0f,
            ttsSpeechPitch = preferences[ThemePreferenceKeys.TTS_SPEECH_PITCH] ?: 1.0f,
            autoSpeakFlashcards = preferences[ThemePreferenceKeys.AUTO_SPEAK_FLASHCARDS] ?: false,
            announceFlashcardSides = preferences[ThemePreferenceKeys.ANNOUNCE_FLASHCARD_SIDES] ?: true,
            keyboardNavigation = preferences[ThemePreferenceKeys.KEYBOARD_NAVIGATION] ?: true,
            focusIndicators = preferences[ThemePreferenceKeys.FOCUS_INDICATORS] ?: true
        )
    }
    
    suspend fun updateDarkTheme(isDark: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.DARK_THEME] = isDark
            }
        }
    }
    
    suspend fun updateDynamicColor(useDynamic: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.DYNAMIC_COLOR] = useDynamic
            }
        }
    }
    
    suspend fun updateHighContrast(useHighContrast: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.HIGH_CONTRAST] = useHighContrast
            }
        }
    }
    
    suspend fun updateFontScale(scale: Float) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.FONT_SCALE] = scale
            }
        }
    }
    
    suspend fun updateReduceMotion(reduce: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.REDUCE_MOTION] = reduce
            }
        }
    }
    
    suspend fun updateColorTheme(theme: String) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.COLOR_THEME] = theme
            }
        }
    }
    
    suspend fun updateEnableTTS(enable: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.ENABLE_TTS] = enable
            }
        }
    }
    
    suspend fun updateTTSSpeechRate(rate: Float) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.TTS_SPEECH_RATE] = rate.coerceIn(0.1f, 3.0f)
            }
        }
    }
    
    suspend fun updateTTSSpeechPitch(pitch: Float) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.TTS_SPEECH_PITCH] = pitch.coerceIn(0.1f, 2.0f)
            }
        }
    }
    
    suspend fun updateAutoSpeakFlashcards(autoSpeak: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.AUTO_SPEAK_FLASHCARDS] = autoSpeak
            }
        }
    }
    
    suspend fun updateAnnounceFlashcardSides(announce: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.ANNOUNCE_FLASHCARD_SIDES] = announce
            }
        }
    }
    
    suspend fun updateKeyboardNavigation(enable: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.KEYBOARD_NAVIGATION] = enable
            }
        }
    }
    
    suspend fun updateFocusIndicators(enable: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[ThemePreferenceKeys.FOCUS_INDICATORS] = enable
            }
        }
    }
}

// Composable to get current theme config
@Composable
fun rememberThemeConfig(): ThemeConfig {
    val context = LocalContext.current
    val repository = ThemePreferencesRepository(context.themeDataStore)
    val config by repository.themeConfig.collectAsState(initial = ThemeConfig())
    return config
}