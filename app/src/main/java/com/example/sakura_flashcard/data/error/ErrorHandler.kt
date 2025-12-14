package com.example.sakura_flashcard.data.error

import android.content.Context
import com.example.sakura_flashcard.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized error handling with user-friendly messages and retry mechanisms
 */
@Singleton
class ErrorHandler @Inject constructor(
    private val context: Context
) {
    
    private val _currentError = MutableStateFlow<AppError?>(null)
    val currentError: StateFlow<AppError?> = _currentError.asStateFlow()
    
    private val _errorHistory = MutableStateFlow<List<AppError>>(emptyList())
    val errorHistory: StateFlow<List<AppError>> = _errorHistory.asStateFlow()
    
    /**
     * Handles an exception and converts it to user-friendly error
     */
    fun handleError(
        throwable: Throwable,
        context: ErrorContext = ErrorContext.GENERAL,
        retryAction: (suspend () -> Unit)? = null
    ): AppError {
        val appError = when (throwable) {
            is NetworkError -> throwable.toAppError(context)
            is HttpException -> handleHttpException(throwable, context)
            is IOException -> handleIOException(throwable, context)
            is ValidationError -> throwable.toAppError(context)
            is AuthenticationError -> throwable.toAppError(context)
            is DataError -> throwable.toAppError(context)
            else -> handleGenericError(throwable, context)
        }.copy(retryAction = retryAction)
        
        // Update current error and history
        _currentError.value = appError
        _errorHistory.value = _errorHistory.value + appError
        
        return appError
    }
    
    /**
     * Handles HTTP exceptions
     */
    private fun handleHttpException(exception: HttpException, context: ErrorContext): AppError {
        val message = when (exception.code()) {
            400 -> context.getString(R.string.error_bad_request)
            401 -> context.getString(R.string.error_unauthorized)
            403 -> context.getString(R.string.error_forbidden)
            404 -> context.getString(R.string.error_not_found)
            408 -> context.getString(R.string.error_timeout)
            429 -> context.getString(R.string.error_rate_limit)
            500 -> context.getString(R.string.error_server_error)
            502, 503, 504 -> context.getString(R.string.error_server_unavailable)
            else -> context.getString(R.string.error_network_generic)
        }
        
        return AppError(
            type = ErrorType.NETWORK,
            message = message,
            context = context,
            severity = when (exception.code()) {
                401, 403 -> ErrorSeverity.HIGH
                500, 502, 503, 504 -> ErrorSeverity.HIGH
                else -> ErrorSeverity.MEDIUM
            },
            isRetryable = exception.code() in listOf(408, 429, 500, 502, 503, 504),
            originalException = exception
        )
    }
    
    /**
     * Handles IO exceptions
     */
    private fun handleIOException(exception: IOException, context: ErrorContext): AppError {
        val (message, isRetryable) = when (exception) {
            is UnknownHostException -> {
                context.getString(R.string.error_no_internet) to true
            }
            is SocketTimeoutException -> {
                context.getString(R.string.error_timeout) to true
            }
            else -> {
                context.getString(R.string.error_network_generic) to true
            }
        }
        
        return AppError(
            type = ErrorType.NETWORK,
            message = message,
            context = context,
            severity = ErrorSeverity.MEDIUM,
            isRetryable = isRetryable,
            originalException = exception
        )
    }
    
    /**
     * Handles generic errors
     */
    private fun handleGenericError(throwable: Throwable, context: ErrorContext): AppError {
        return AppError(
            type = ErrorType.UNKNOWN,
            message = context.getString(R.string.error_generic),
            context = context,
            severity = ErrorSeverity.MEDIUM,
            isRetryable = false,
            originalException = throwable
        )
    }
    
    /**
     * Clears current error
     */
    fun clearCurrentError() {
        _currentError.value = null
    }
    
    /**
     * Clears error history
     */
    fun clearErrorHistory() {
        _errorHistory.value = emptyList()
    }
    
    /**
     * Gets error statistics
     */
    fun getErrorStats(): ErrorStats {
        val history = _errorHistory.value
        return ErrorStats(
            totalErrors = history.size,
            networkErrors = history.count { it.type == ErrorType.NETWORK },
            authErrors = history.count { it.type == ErrorType.AUTHENTICATION },
            dataErrors = history.count { it.type == ErrorType.DATA },
            validationErrors = history.count { it.type == ErrorType.VALIDATION },
            highSeverityErrors = history.count { it.severity == ErrorSeverity.HIGH },
            retryableErrors = history.count { it.isRetryable }
        )
    }
    
    private fun ErrorContext.getString(resId: Int): String {
        return when (this) {
            ErrorContext.FLASHCARD_LOADING -> context.getString(resId) + " " + context.getString(R.string.error_context_flashcards)
            ErrorContext.USER_AUTHENTICATION -> context.getString(resId) + " " + context.getString(R.string.error_context_auth)
            ErrorContext.QUIZ_SUBMISSION -> context.getString(resId) + " " + context.getString(R.string.error_context_quiz)
            ErrorContext.GAME_SUBMISSION -> context.getString(resId) + " " + context.getString(R.string.error_context_game)
            ErrorContext.PROFILE_UPDATE -> context.getString(resId) + " " + context.getString(R.string.error_context_profile)
            ErrorContext.SYNC_OPERATION -> context.getString(resId) + " " + context.getString(R.string.error_context_sync)
            ErrorContext.CHARACTER_LOADING -> context.getString(resId) + " " + context.getString(R.string.error_context_characters)
            ErrorContext.IMAGE_LOADING -> context.getString(resId) + " " + context.getString(R.string.error_context_images)
            ErrorContext.GENERAL -> context.getString(resId)
        }
    }
}

/**
 * Represents an application error with user-friendly information
 */
data class AppError(
    val type: ErrorType,
    val message: String,
    val context: ErrorContext,
    val severity: ErrorSeverity,
    val isRetryable: Boolean,
    val retryAction: (suspend () -> Unit)? = null,
    val originalException: Throwable? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Types of errors
 */
enum class ErrorType {
    NETWORK,
    AUTHENTICATION,
    VALIDATION,
    DATA,
    UNKNOWN
}

/**
 * Error severity levels
 */
enum class ErrorSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Context where error occurred
 */
enum class ErrorContext {
    GENERAL,
    FLASHCARD_LOADING,
    USER_AUTHENTICATION,
    QUIZ_SUBMISSION,
    GAME_SUBMISSION,
    PROFILE_UPDATE,
    SYNC_OPERATION,
    CHARACTER_LOADING,
    IMAGE_LOADING
}

/**
 * Error statistics
 */
data class ErrorStats(
    val totalErrors: Int,
    val networkErrors: Int,
    val authErrors: Int,
    val dataErrors: Int,
    val validationErrors: Int,
    val highSeverityErrors: Int,
    val retryableErrors: Int
)

// Custom exception types

/**
 * Network-related errors
 */
class NetworkError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    fun toAppError(context: ErrorContext): AppError {
        return AppError(
            type = ErrorType.NETWORK,
            message = message ?: "Network error occurred",
            context = context,
            severity = ErrorSeverity.MEDIUM,
            isRetryable = true,
            originalException = this
        )
    }
}

/**
 * Authentication-related errors
 */
class AuthenticationError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    fun toAppError(context: ErrorContext): AppError {
        return AppError(
            type = ErrorType.AUTHENTICATION,
            message = message ?: "Authentication error occurred",
            context = context,
            severity = ErrorSeverity.HIGH,
            isRetryable = false,
            originalException = this
        )
    }
}

/**
 * Data validation errors
 */
class ValidationError(
    message: String,
    val field: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    fun toAppError(context: ErrorContext): AppError {
        return AppError(
            type = ErrorType.VALIDATION,
            message = message ?: "Validation error occurred",
            context = context,
            severity = ErrorSeverity.LOW,
            isRetryable = false,
            originalException = this
        )
    }
}

/**
 * Data-related errors
 */
class DataError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    fun toAppError(context: ErrorContext): AppError {
        return AppError(
            type = ErrorType.DATA,
            message = message ?: "Data error occurred",
            context = context,
            severity = ErrorSeverity.MEDIUM,
            isRetryable = true,
            originalException = this
        )
    }
}