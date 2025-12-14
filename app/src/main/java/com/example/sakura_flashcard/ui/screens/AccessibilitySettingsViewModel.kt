package com.example.sakura_flashcard.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessibilitySettingsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    
    private val themePreferencesRepository = ThemePreferencesRepository(
        application.applicationContext.themeDataStore
    )
    
    val themeConfig: StateFlow<ThemeConfig> = themePreferencesRepository.themeConfig.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeConfig()
    )
    
    suspend fun updateHighContrast(useHighContrast: Boolean) {
        themePreferencesRepository.updateHighContrast(useHighContrast)
    }
    
    suspend fun updateFontScale(scale: Float) {
        val validatedScale = AccessibilityUtils.validateFontScale(scale)
        themePreferencesRepository.updateFontScale(validatedScale)
    }
    
    suspend fun updateReduceMotion(reduce: Boolean) {
        themePreferencesRepository.updateReduceMotion(reduce)
    }
    
    suspend fun updateEnableTTS(enable: Boolean) {
        themePreferencesRepository.updateEnableTTS(enable)
    }
    
    suspend fun updateTTSSpeechRate(rate: Float) {
        themePreferencesRepository.updateTTSSpeechRate(rate)
    }
    
    suspend fun updateTTSSpeechPitch(pitch: Float) {
        themePreferencesRepository.updateTTSSpeechPitch(pitch)
    }
    
    suspend fun updateAutoSpeakFlashcards(autoSpeak: Boolean) {
        themePreferencesRepository.updateAutoSpeakFlashcards(autoSpeak)
    }
    
    suspend fun updateAnnounceFlashcardSides(announce: Boolean) {
        themePreferencesRepository.updateAnnounceFlashcardSides(announce)
    }
    
    suspend fun updateKeyboardNavigation(enable: Boolean) {
        themePreferencesRepository.updateKeyboardNavigation(enable)
    }
    
    suspend fun updateFocusIndicators(enable: Boolean) {
        themePreferencesRepository.updateFocusIndicators(enable)
    }
}

