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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _username = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isRegistrationSuccessful = MutableStateFlow(false)

    val uiState: StateFlow<RegisterUiState> = combine(
        _username,
        _email,
        _password,
        _confirmPassword,
        _isLoading,
        _errorMessage,
        _isRegistrationSuccessful
    ) { flows ->
        val username = flows[0] as String
        val email = flows[1] as String
        val password = flows[2] as String
        val confirmPassword = flows[3] as String
        val isLoading = flows[4] as Boolean
        val errorMessage = flows[5] as String?
        val isRegistrationSuccessful = flows[6] as Boolean
        
        RegisterUiState(
            username = username,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isRegistrationSuccessful = isRegistrationSuccessful,
            usernameError = validateUsername(username),
            emailError = validateEmail(email),
            passwordError = validatePassword(password),
            confirmPasswordError = validateConfirmPassword(password, confirmPassword),
            isFormValid = validateUsername(username) == null && 
                         validateEmail(email) == null && 
                         validatePassword(password) == null && 
                         validateConfirmPassword(password, confirmPassword) == null &&
                         username.isNotBlank() && 
                         email.isNotBlank() && 
                         password.isNotBlank() && 
                         confirmPassword.isNotBlank()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RegisterUiState()
    )

    fun updateUsername(username: String) {
        _username.value = username
        clearErrorMessage()
    }

    fun updateEmail(email: String) {
        _email.value = email
        clearErrorMessage()
    }

    fun updatePassword(password: String) {
        _password.value = password
        clearErrorMessage()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        clearErrorMessage()
    }

    fun register() {
        if (_isLoading.value) return
        
        val currentUsername = _username.value.trim()
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value
        
        // Validate inputs
        val usernameError = validateUsername(currentUsername)
        val emailError = validateEmail(currentEmail)
        val passwordError = validatePassword(currentPassword)
        val confirmPasswordError = validateConfirmPassword(currentPassword, currentConfirmPassword)
        
        val firstError = usernameError ?: emailError ?: passwordError ?: confirmPasswordError
        if (firstError != null) {
            _errorMessage.value = firstError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = authRepository.register(currentUsername, currentEmail, currentPassword)) {
                is AuthResult.Success -> {
                    _isRegistrationSuccessful.value = true
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                is AuthResult.TokenRefreshed -> {
                    // Token was refreshed during registration process
                }
            }
            
            _isLoading.value = false
        }
    }

    private fun clearErrorMessage() {
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isBlank() -> null // Don't show error for empty field
            !ContentValidator.isValidUsername(username) -> "Username must be 3-20 characters and contain only letters, numbers, and underscores"
            else -> null
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
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> null // Don't show error for empty field
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isFormValid: Boolean = false
)