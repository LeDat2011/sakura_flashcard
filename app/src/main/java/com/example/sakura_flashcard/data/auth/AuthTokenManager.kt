package com.example.sakura_flashcard.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val TOKEN_EXPIRY_KEY = "token_expiry"
        private const val USER_ID_KEY = "user_id"
        private const val SESSION_EXPIRY_KEY = "session_expiry"
        private const val LAST_ACTIVITY_KEY = "last_activity"
        private const val BIOMETRIC_ENABLED_KEY = "biometric_enabled"
        private const val SAVED_EMAIL_KEY = "saved_email"
        private const val SAVED_PASSWORD_KEY = "saved_password"
        
        // Session duy trì 7 ngày nếu không hoạt động
        private const val SESSION_DURATION_DAYS = 7L
        private const val SESSION_DURATION_SECONDS = SESSION_DURATION_DAYS * 24 * 60 * 60
    }

    init {
        // Check if user is already authenticated on initialization
        checkAndRestoreSession()
    }

    private fun checkAndRestoreSession() {
        val accessToken = getAccessToken()
        val userId = getUserId()
        
        if (accessToken != null && userId != null) {
            // Kiểm tra session 7 ngày
            if (isSessionValid()) {
                _authState.value = AuthState.Authenticated(userId, accessToken)
                // Cập nhật last activity
                updateLastActivity()
            } else {
                // Session hết hạn, clear tokens
                clearTokens()
            }
        }
    }

    /**
     * Kiểm tra session còn hợp lệ không (7 ngày không hoạt động)
     */
    fun isSessionValid(): Boolean {
        val lastActivity = encryptedPrefs.getLong(LAST_ACTIVITY_KEY, 0)
        if (lastActivity == 0L) return false
        
        val now = Instant.now().epochSecond
        val inactiveTime = now - lastActivity
        
        return inactiveTime < SESSION_DURATION_SECONDS
    }

    /**
     * Cập nhật thời gian hoạt động cuối cùng
     */
    fun updateLastActivity() {
        encryptedPrefs.edit().apply {
            putLong(LAST_ACTIVITY_KEY, Instant.now().epochSecond)
            apply()
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long, userId: String) {
        val tokenExpiryTime = Instant.now().plusSeconds(expiresIn).epochSecond
        val sessionExpiryTime = Instant.now().plusSeconds(SESSION_DURATION_SECONDS).epochSecond
        val now = Instant.now().epochSecond
        
        encryptedPrefs.edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            putLong(TOKEN_EXPIRY_KEY, tokenExpiryTime)
            putString(USER_ID_KEY, userId)
            putLong(SESSION_EXPIRY_KEY, sessionExpiryTime)
            putLong(LAST_ACTIVITY_KEY, now)
            apply()
        }
        
        _authState.value = AuthState.Authenticated(userId, accessToken)
    }

    // ==================== Biometric Authentication ====================

    /**
     * Bật/tắt đăng nhập bằng vân tay
     */
    fun setBiometricEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean(BIOMETRIC_ENABLED_KEY, enabled).apply()
    }

    /**
     * Kiểm tra đăng nhập vân tay có được bật không
     */
    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(BIOMETRIC_ENABLED_KEY, false)
    }

    /**
     * Lưu credentials để dùng cho biometric login
     */
    fun saveCredentialsForBiometric(email: String, password: String) {
        encryptedPrefs.edit().apply {
            putString(SAVED_EMAIL_KEY, email)
            putString(SAVED_PASSWORD_KEY, password)
            apply()
        }
    }

    /**
     * Lấy credentials đã lưu
     */
    fun getSavedCredentials(): Pair<String, String>? {
        val email = encryptedPrefs.getString(SAVED_EMAIL_KEY, null)
        val password = encryptedPrefs.getString(SAVED_PASSWORD_KEY, null)
        
        return if (email != null && password != null) {
            Pair(email, password)
        } else {
            null
        }
    }

    /**
     * Kiểm tra có credentials đã lưu không
     */
    fun hasSavedCredentials(): Boolean {
        return getSavedCredentials() != null
    }

    /**
     * Xóa credentials đã lưu
     */
    fun clearSavedCredentials() {
        encryptedPrefs.edit().apply {
            remove(SAVED_EMAIL_KEY)
            remove(SAVED_PASSWORD_KEY)
            remove(BIOMETRIC_ENABLED_KEY)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return encryptedPrefs.getString(ACCESS_TOKEN_KEY, null)
    }

    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(REFRESH_TOKEN_KEY, null)
    }

    fun getUserId(): String? {
        return encryptedPrefs.getString(USER_ID_KEY, null)
    }

    fun isTokenExpired(): Boolean {
        val expiryTime = encryptedPrefs.getLong(TOKEN_EXPIRY_KEY, 0)
        return Instant.now().epochSecond >= expiryTime
    }

    fun isAuthenticated(): Boolean {
        return getAccessToken() != null && getUserId() != null && !isTokenExpired()
    }

    fun clearTokens() {
        encryptedPrefs.edit().apply {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            remove(TOKEN_EXPIRY_KEY)
            remove(USER_ID_KEY)
            remove(SESSION_EXPIRY_KEY)
            remove(LAST_ACTIVITY_KEY)
            // Không xóa biometric credentials để user có thể đăng nhập lại bằng vân tay
            apply()
        }
        
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Xóa hoàn toàn tất cả dữ liệu (bao gồm cả biometric)
     */
    fun clearAllData() {
        encryptedPrefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    fun updateAuthState(state: AuthState) {
        _authState.value = state
    }

    /**
     * Lấy số ngày còn lại của session
     */
    fun getRemainingSessionDays(): Int {
        val lastActivity = encryptedPrefs.getLong(LAST_ACTIVITY_KEY, 0)
        if (lastActivity == 0L) return 0
        
        val now = Instant.now().epochSecond
        val inactiveTime = now - lastActivity
        val remainingSeconds = SESSION_DURATION_SECONDS - inactiveTime
        
        return if (remainingSeconds > 0) {
            (remainingSeconds / (24 * 60 * 60)).toInt()
        } else {
            0
        }
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val userId: String, val accessToken: String) : AuthState()
    data class Error(val message: String) : AuthState()
}