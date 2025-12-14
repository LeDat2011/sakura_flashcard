package com.example.sakura_flashcard.data.auth

import com.example.sakura_flashcard.data.api.*
import com.example.sakura_flashcard.data.validation.ContentValidator
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {
    val authState: StateFlow<AuthState> = tokenManager.authState

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            if (!ContentValidator.isValidEmail(email)) {
                return AuthResult.Error("Invalid email format")
            }
            if (password.isBlank()) {
                return AuthResult.Error("Password cannot be empty")
            }

            tokenManager.updateAuthState(AuthState.Loading)

            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                if (authData != null) {
                    tokenManager.saveTokens(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        expiresIn = 900, // 15 minutes
                        userId = authData.user.id
                    )
                    AuthResult.Success(authData.user)
                } else {
                    tokenManager.updateAuthState(AuthState.Error("Invalid response from server"))
                    AuthResult.Error("Invalid response from server")
                }
            } else {
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val jsonObject = org.json.JSONObject(errorBody)
                        jsonObject.optString("message", "Login failed")
                    } else {
                        response.body()?.message ?: "Login failed"
                    }
                } catch (e: Exception) {
                    "Login failed"
                }
                tokenManager.updateAuthState(AuthState.Error(errorMessage))
                AuthResult.Error(errorMessage)
            }
        } catch (e: HttpException) {
            handleHttpError(e)
        } catch (e: IOException) {
            tokenManager.updateAuthState(AuthState.Error("Network error. Please check your connection"))
            AuthResult.Error("Network error. Please check your connection")
        } catch (e: Exception) {
            tokenManager.updateAuthState(AuthState.Error("An unexpected error occurred"))
            AuthResult.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun register(username: String, email: String, password: String, displayName: String? = null): AuthResult {
        return try {
            if (!ContentValidator.isValidUsername(username)) {
                return AuthResult.Error("Username must be 3-20 characters")
            }
            if (!ContentValidator.isValidEmail(email)) {
                return AuthResult.Error("Invalid email format")
            }
            if (password.length < 6) {
                return AuthResult.Error("Password must be at least 6 characters")
            }

            tokenManager.updateAuthState(AuthState.Loading)

            val response = apiService.register(
                RegisterRequest(
                    email = email,
                    username = username,
                    password = password,
                    displayName = displayName ?: username
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                if (authData != null) {
                    tokenManager.saveTokens(
                        accessToken = authData.accessToken,
                        refreshToken = authData.refreshToken,
                        expiresIn = 900,
                        userId = authData.user.id
                    )
                    AuthResult.Success(authData.user)
                } else {
                    tokenManager.updateAuthState(AuthState.Error("Invalid response from server"))
                    AuthResult.Error("Invalid response from server")
                }
            } else {
                val errorMessage = try {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val jsonObject = org.json.JSONObject(errorBody)
                        jsonObject.optString("message", "Registration failed")
                    } else {
                        response.body()?.message ?: "Registration failed"
                    }
                } catch (e: Exception) {
                    "Registration failed"
                }
                tokenManager.updateAuthState(AuthState.Error(errorMessage))
                AuthResult.Error(errorMessage)
            }
        } catch (e: HttpException) {
            handleHttpError(e)
        } catch (e: IOException) {
            tokenManager.updateAuthState(AuthState.Error("Network error"))
            AuthResult.Error("Network error. Please check your connection")
        } catch (e: Exception) {
            tokenManager.updateAuthState(AuthState.Error("An unexpected error occurred"))
            AuthResult.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun refreshToken(): AuthResult {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: return AuthResult.Error("No refresh token available").also { logout() }

            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.isSuccessful && response.body()?.success == true) {
                val tokenData = response.body()?.data
                if (tokenData != null) {
                    val userId = tokenManager.getUserId() ?: ""
                    tokenManager.saveTokens(
                        accessToken = tokenData.accessToken,
                        refreshToken = tokenData.refreshToken,
                        expiresIn = 900,
                        userId = userId
                    )
                    AuthResult.TokenRefreshed
                } else {
                    logout()
                    AuthResult.Error("Session expired")
                }
            } else {
                logout()
                AuthResult.Error("Session expired. Please login again")
            }
        } catch (e: Exception) {
            logout()
            AuthResult.Error("Session expired. Please login again")
        }
    }

    suspend fun getProfile(): AuthResult {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    AuthResult.Success(it)
                } ?: AuthResult.Error("Failed to get profile")
            } else {
                AuthResult.Error(response.body()?.message ?: "Failed to get profile")
            }
        } catch (e: Exception) {
            AuthResult.Error("Failed to get profile: ${e.message}")
        }
    }

    suspend fun logout(): AuthResult {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                try {
                    apiService.logout(LogoutRequest(refreshToken))
                } catch (e: Exception) {
                    // Ignore server errors
                }
            }
            tokenManager.clearTokens()
            AuthResult.Success(null)
        } catch (e: Exception) {
            tokenManager.clearTokens()
            AuthResult.Success(null)
        }
    }

    fun isAuthenticated(): Boolean = tokenManager.isAuthenticated()

    fun getCurrentUserId(): String? = tokenManager.getUserId()

    private fun handleHttpError(e: HttpException): AuthResult {
        val errorMessage = when (e.code()) {
            401 -> "Invalid credentials"
            409 -> "Email or username already exists"
            429 -> "Too many attempts. Please try again later"
            else -> "Server error. Please try again"
        }
        tokenManager.updateAuthState(AuthState.Error(errorMessage))
        return AuthResult.Error(errorMessage)
    }
}

sealed class AuthResult {
    data class Success(val user: UserProfile?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object TokenRefreshed : AuthResult()
}