package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthResult
import com.example.sakura_flashcard.data.validation.ContentValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoginSuccessful = MutableStateFlow(false)

    val uiState: StateFlow<LoginUiState> = combine(
        _email,
        _password,
        _isLoading,
        _errorMessage,
        _isLoginSuccessful
    ) { flows ->
        val email = flows[0] as String
        val password = flows[1] as String
        val isLoading = flows[2] as Boolean
        val errorMessage = flows[3] as String?
        val isLoginSuccessful = flows[4] as Boolean
        
        LoginUiState(
            email = email,
            password = password,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isLoginSuccessful = isLoginSuccessful,
            emailError = validateEmail(email),
            passwordError = validatePassword(password),
            isFormValid = validateEmail(email) == null && 
                         validatePassword(password) == null && 
                         email.isNotBlank() && 
                         password.isNotBlank()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginUiState()
    )

    fun updateEmail(email: String) {
        _email.value = email
        // Clear error message when user starts typing
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
        // Clear error message when user starts typing
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun login() {
        if (_isLoading.value) return
        
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        
        // Validate inputs
        val emailError = validateEmail(currentEmail)
        val passwordError = validatePassword(currentPassword)
        
        if (emailError != null || passwordError != null) {
            _errorMessage.value = emailError ?: passwordError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = authRepository.login(currentEmail, currentPassword)) {
                is AuthResult.Success -> {
                    _isLoginSuccessful.value = true
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                is AuthResult.TokenRefreshed -> {
                    // Token was refreshed, retry login or ignore
                }
            }
            
            _isLoading.value = false
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null // Don't show error for empty field
            !ContentValidator.isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null // Don't show error for empty field
            password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false
)