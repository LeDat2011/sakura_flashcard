package com.example.sakura_flashcard.data.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)

    /**
     * Kiểm tra thiết bị có hỗ trợ biometric không
     */
    fun isBiometricAvailable(): BiometricStatus {
        // Thử BIOMETRIC_STRONG trước, nếu không được thì thử BIOMETRIC_WEAK
        val strongResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val weakResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        
        return when {
            strongResult == BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            weakResult == BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
            strongResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED || 
                weakResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NoneEnrolled
            strongResult == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NoHardware
            strongResult == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HardwareUnavailable
            strongResult == BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SecurityUpdateRequired
            else -> BiometricStatus.Unknown
        }
    }

    /**
     * Hiển thị prompt xác thực vân tay
     */
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String = "Xác thực vân tay",
        subtitle: String = "Sử dụng vân tay để đăng nhập",
        negativeButtonText: String = "Hủy"
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Success)
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (continuation.isActive) {
                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.Cancelled
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricResult.Lockout(errString.toString())
                        else -> BiometricResult.Error(errString.toString())
                    }
                    continuation.resume(result)
                }
            }

            override fun onAuthenticationFailed() {
                // Không resume ở đây vì user có thể thử lại
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        // Sử dụng BIOMETRIC_WEAK để hỗ trợ nhiều thiết bị hơn
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        biometricPrompt.authenticate(promptInfo)

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }
    }
}

sealed class BiometricStatus {
    object Available : BiometricStatus()
    object NoHardware : BiometricStatus()
    object HardwareUnavailable : BiometricStatus()
    object NoneEnrolled : BiometricStatus()
    object SecurityUpdateRequired : BiometricStatus()
    object Unknown : BiometricStatus()
}

sealed class BiometricResult {
    object Success : BiometricResult()
    object Cancelled : BiometricResult()
    data class Lockout(val message: String) : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}
