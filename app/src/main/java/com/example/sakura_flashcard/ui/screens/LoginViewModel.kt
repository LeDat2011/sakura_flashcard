package com.example.sakura_flashcard.ui.screens

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthResult
import com.example.sakura_flashcard.data.auth.BiometricStatus
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
    private val _otp = MutableStateFlow("")
    private val _isOtpSent = MutableStateFlow(false)
    private val _enableBiometric = MutableStateFlow(false)

    // Kiểm tra có thể dùng biometric login không
    private val canUseBiometricLogin: Boolean
        get() = authRepository.canUseBiometricLogin()

    // Kiểm tra thiết bị có hỗ trợ biometric không
    val isBiometricAvailable: Boolean
        get() = authRepository.isBiometricAvailable() == BiometricStatus.Available

    val uiState: StateFlow<LoginUiState> = combine(
        _email,
        _password,
        _isLoading,
        _errorMessage,
        _isLoginSuccessful,
        _isOtpSent,
        _enableBiometric
    ) { flows ->
        val email = flows[0] as String
        val password = flows[1] as String
        val isLoading = flows[2] as Boolean
        val errorMessage = flows[3] as String?
        val isLoginSuccessful = flows[4] as Boolean
        val isOtpSent = flows[5] as Boolean
        val enableBiometric = flows[6] as Boolean
        
        val emailErr = validateEmail(email)
        val passwordErr = validatePassword(password)
        
        LoginUiState(
            email = email,
            password = password,
            isLoading = isLoading,
            errorMessage = errorMessage,
            isLoginSuccessful = isLoginSuccessful,
            emailError = emailErr,
            passwordError = passwordErr,
            isFormValid = emailErr == null && 
                         passwordErr == null && 
                         email.isNotBlank() && 
                         password.isNotBlank(),
            canUseBiometric = canUseBiometricLogin,
            isBiometricAvailable = isBiometricAvailable,
            isOtpSent = isOtpSent,
            enableBiometric = enableBiometric
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginUiState(
            canUseBiometric = canUseBiometricLogin,
            isBiometricAvailable = isBiometricAvailable
        )
    )

    fun updateEmail(email: String) {
        _email.value = email
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun updatePassword(password: String) {
        _password.value = password
        if (_errorMessage.value != null) {
            _errorMessage.value = null
        }
    }

    fun updateEnableBiometric(enabled: Boolean) {
        _enableBiometric.value = enabled
    }

    fun login() {
        if (_isLoading.value) return
        
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value
        
        val emailError = validateEmail(currentEmail)
        val passwordError = validatePassword(currentPassword)
        
        if (emailError != null || passwordError != null) {
            _errorMessage.value = emailError ?: passwordError
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Đăng nhập với option bật biometric
            when (val result = authRepository.login(
                email = currentEmail, 
                password = currentPassword,
                enableBiometric = _enableBiometric.value && isBiometricAvailable
            )) {
                is AuthResult.Success -> {
                    _isLoginSuccessful.value = true
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
                is AuthResult.TokenRefreshed -> {
                    // Token was refreshed
                }
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Đăng nhập bằng vân tay/biometric
     */
    fun biometricLogin(activity: FragmentActivity) {
        android.util.Log.d("BiometricLogin", "biometricLogin() called")
        
        if (_isLoading.value) {
            android.util.Log.d("BiometricLogin", "Already loading, returning early")
            return
        }
        
        viewModelScope.launch {
            android.util.Log.d("BiometricLogin", "Starting biometric authentication...")
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                android.util.Log.d("BiometricLogin", "Calling authRepository.loginWithBiometric...")
                when (val result = authRepository.loginWithBiometric(activity)) {
                    is AuthResult.Success -> {
                        android.util.Log.d("BiometricLogin", "SUCCESS!")
                        _isLoginSuccessful.value = true
                    }
                    is AuthResult.Error -> {
                        android.util.Log.e("BiometricLogin", "ERROR: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    is AuthResult.TokenRefreshed -> {
                        android.util.Log.d("BiometricLogin", "Token refreshed")
                        _isLoginSuccessful.value = true
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("BiometricLogin", "EXCEPTION: ${e.message}", e)
                _errorMessage.value = "Lỗi: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.googleLogin(idToken)) {
                is AuthResult.Success -> _isLoginSuccessful.value = true
                is AuthResult.Error -> _errorMessage.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun sendOTP() {
        val currentEmail = _email.value.trim()
        if (validateEmail(currentEmail) != null) {
            _errorMessage.value = "Vui lòng nhập email hợp lệ để nhận OTP"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.sendOTP(currentEmail)) {
                is AuthResult.Success -> _isOtpSent.value = true
                is AuthResult.Error -> _errorMessage.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun verifyOTP(otp: String) {
        val currentEmail = _email.value.trim()
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.verifyOTP(currentEmail, otp)) {
                is AuthResult.Success -> _isLoginSuccessful.value = true
                is AuthResult.Error -> _errorMessage.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== FORGOT PASSWORD ====================
    private val _isForgotPasswordSent = MutableStateFlow(false)
    private val _isResetPasswordSuccess = MutableStateFlow(false)
    
    val isForgotPasswordSent: StateFlow<Boolean> = _isForgotPasswordSent
    val isResetPasswordSuccess: StateFlow<Boolean> = _isResetPasswordSuccess
    
    fun forgotPassword() {
        val currentEmail = _email.value.trim()
        val emailError = validateEmail(currentEmail)
        
        if (currentEmail.isBlank() || emailError != null) {
            _errorMessage.value = emailError ?: "Vui lòng nhập email để đặt lại mật khẩu"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.forgotPassword(currentEmail)) {
                is AuthResult.Success -> _isForgotPasswordSent.value = true
                is AuthResult.Error -> _errorMessage.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        val currentEmail = _email.value.trim()
        if (newPassword.length < 8) {
            _errorMessage.value = "Mật khẩu mới phải có ít nhất 8 ký tự"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = authRepository.resetPassword(currentEmail, token, newPassword)) {
                is AuthResult.Success -> {
                    _isResetPasswordSuccess.value = true
                    _isForgotPasswordSent.value = false
                }
                is AuthResult.Error -> _errorMessage.value = result.message
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun clearForgotPasswordState() {
        _isForgotPasswordSent.value = false
        _isResetPasswordSuccess.value = false
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Vui lòng nhập email"
            !ContentValidator.isValidEmail(email) -> "Vui lòng nhập email hợp lệ"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null
            password.length < 8 -> "Mật khẩu phải có ít nhất 8 ký tự"
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
    val isFormValid: Boolean = false,
    val canUseBiometric: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isOtpSent: Boolean = false,
    val enableBiometric: Boolean = false
)