package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UserOnboardingViewModel @Inject constructor(
    // Add repositories here when available
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    fun saveUserProfile(
        userId: String,
        displayName: String,
        age: Int?,
        currentLevel: JLPTLevel,
        targetLevel: JLPTLevel,
        dailyGoalMinutes: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val profile = UserProfile(
                    userId = userId,
                    displayName = displayName,
                    age = age,
                    currentLevel = currentLevel,
                    targetLevel = targetLevel,
                    dailyStudyGoalMinutes = dailyGoalMinutes,
                    isOnboardingCompleted = true
                )
                
                // TODO: Save profile to repository
                // userProfileRepository.saveProfile(profile)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompleted = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Không thể lưu thông tin: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
