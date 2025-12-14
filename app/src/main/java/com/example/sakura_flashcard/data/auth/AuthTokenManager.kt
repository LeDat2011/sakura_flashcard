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
    }

    init {
        // Check if user is already authenticated on initialization
        val accessToken = getAccessToken()
        val userId = getUserId()
        if (accessToken != null && userId != null && !isTokenExpired()) {
            _authState.value = AuthState.Authenticated(userId, accessToken)
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Long, userId: String) {
        val expiryTime = Instant.now().plusSeconds(expiresIn).epochSecond
        
        encryptedPrefs.edit().apply {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            putLong(TOKEN_EXPIRY_KEY, expiryTime)
            putString(USER_ID_KEY, userId)
            apply()
        }
        
        _authState.value = AuthState.Authenticated(userId, accessToken)
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
            apply()
        }
        
        _authState.value = AuthState.Unauthenticated
    }

    fun updateAuthState(state: AuthState) {
        _authState.value = state
    }
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val userId: String, val accessToken: String) : AuthState()
    data class Error(val message: String) : AuthState()
}