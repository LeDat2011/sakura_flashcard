package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.api.UserProfile
import com.example.sakura_flashcard.data.api.UserStatsDto
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthResult
import com.example.sakura_flashcard.data.repository.CustomDeckRepository
import com.example.sakura_flashcard.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository,
    private val customDeckRepository: CustomDeckRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()

    private val _customDecks = MutableStateFlow<List<CustomDeck>>(emptyList())
    val customDecks: StateFlow<List<CustomDeck>> = _customDecks.asStateFlow()

    // Biometric state
    private val _isBiometricEnabled = MutableStateFlow(authRepository.isBiometricEnabled())
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()
    
    val isBiometricAvailable: Boolean
        get() = authRepository.isBiometricAvailable() == com.example.sakura_flashcard.data.auth.BiometricStatus.Available

    init {
        loadUserProfile()
        loadUserStats()
        loadCustomDecks()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = authRepository.getProfile()) {
                is AuthResult.Success -> {
                    result.user?.let { profile ->
                        _uiState.value = _uiState.value.copy(
                            userProfile = profile,
                            isLoading = false
                        )
                        // Initialize edit state
                        _editState.value = ProfileEditState(
                            username = profile.username,
                            displayName = profile.profile.displayName,
                            email = profile.email,
                            avatar = profile.profile.avatar
                        )
                    }
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {}
            }
        }
    }

    private fun loadUserStats() {
        viewModelScope.launch {
            quizRepository.getUserStats()
                .onSuccess { stats ->
                    _uiState.value = _uiState.value.copy(userStats = stats)
                }
        }
    }

    private fun loadCustomDecks() {
        viewModelScope.launch {
            try {
                customDeckRepository.getAllDecksWithFlashcards().collect { decks ->
                    _customDecks.value = decks
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
        loadUserStats()
    }

    // Edit profile methods
    fun updateUsername(username: String) {
        _editState.value = _editState.value.copy(username = username)
    }

    fun updateDisplayName(displayName: String) {
        _editState.value = _editState.value.copy(displayName = displayName)
    }

    fun updateEmail(email: String) {
        _editState.value = _editState.value.copy(email = email)
    }

    fun updateAvatar(avatar: String?) {
        _editState.value = _editState.value.copy(avatar = avatar)
    }

    fun saveProfile() {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true, error = null)
            
            val currentEditState = _editState.value
            
            when (val result = authRepository.updateProfile(
                username = currentEditState.username,
                displayName = currentEditState.displayName,
                avatar = currentEditState.avatar
            )) {
                is AuthResult.Success -> {
                    _editState.value = _editState.value.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                    // Refresh profile to get updated data
                    loadUserProfile()
                }
                is AuthResult.Error -> {
                    _editState.value = _editState.value.copy(
                        isSaving = false,
                        saveSuccess = false,
                        error = result.message
                    )
                }
                else -> {
                    _editState.value = _editState.value.copy(isSaving = false)
                }
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isChangingPassword = true)
            
            // TODO: Call API to change password
            if (newPassword.length >= 6) {
                kotlinx.coroutines.delay(1000)
                _editState.value = _editState.value.copy(
                    isChangingPassword = false,
                    passwordChangeSuccess = true
                )
            } else {
                _editState.value = _editState.value.copy(
                    isChangingPassword = false,
                    error = "Mật khẩu phải có ít nhất 6 ký tự"
                )
            }
        }
    }

    // Custom deck methods
    fun createCustomDeck(name: String) {
        viewModelScope.launch {
            try {
                customDeckRepository.createDeck(name)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCustomDeck(deckId: String) {
        viewModelScope.launch {
            try {
                customDeckRepository.deleteDeck(deckId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addFlashcardToDeck(deckId: String, japanese: String, romaji: String, vietnamese: String) {
        viewModelScope.launch {
            try {
                customDeckRepository.addFlashcardToDeck(deckId, japanese, romaji, vietnamese)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun removeFlashcardFromDeck(deckId: String, flashcardId: String) {
        viewModelScope.launch {
            try {
                customDeckRepository.removeFlashcard(flashcardId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun getDeckById(deckId: String): CustomDeck? {
        return _customDecks.value.find { it.id == deckId }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            authRepository.logout()
            _uiState.value = ProfileUiState(isLoggedOut = true)
            _editState.value = ProfileEditState()
            _customDecks.value = emptyList()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        _editState.value = _editState.value.copy(error = null)
    }

    fun toggleBiometric(enabled: Boolean, email: String, password: String) {
        if (enabled) {
            authRepository.enableBiometricLogin(email, password)
        } else {
            authRepository.disableBiometricLogin()
        }
        _isBiometricEnabled.value = enabled
    }

    fun clearSuccessMessages() {
        _editState.value = _editState.value.copy(
            saveSuccess = false,
            passwordChangeSuccess = false
        )
    }
}

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val userStats: UserStatsDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)

data class ProfileEditState(
    val username: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatar: String? = null,
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val saveSuccess: Boolean = false,
    val passwordChangeSuccess: Boolean = false,
    val error: String? = null
)

data class CustomDeck(
    val id: String,
    val name: String,
    val flashcards: List<CustomFlashcard> = emptyList(),
    val createdAt: Instant
) {
    val flashcardCount: Int get() = flashcards.size
}

data class CustomFlashcard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val japanese: String,
    val romaji: String,
    val vietnamese: String,
    val createdAt: Instant = Instant.now()
)