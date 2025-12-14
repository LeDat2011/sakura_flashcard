package com.example.sakura_flashcard.data.error

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Manages retry mechanisms for network failures and other recoverable errors
 */
@Singleton
class RetryManager @Inject constructor() {
    
    companion object {
        private const val DEFAULT_MAX_RETRIES = 3
        private const val DEFAULT_INITIAL_DELAY = 1000L
        private const val DEFAULT_MAX_DELAY = 30000L
        private const val DEFAULT_BACKOFF_MULTIPLIER = 2.0
        private const val DEFAULT_JITTER_FACTOR = 0.1
    }
    
    /**
     * Executes an operation with exponential backoff retry
     */
    suspend fun <T> executeWithRetry(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        initialDelay: Long = DEFAULT_INITIAL_DELAY,
        maxDelay: Long = DEFAULT_MAX_DELAY,
        backoffMultiplier: Double = DEFAULT_BACKOFF_MULTIPLIER,
        jitterFactor: Double = DEFAULT_JITTER_FACTOR,
        retryCondition: (Throwable) -> Boolean = ::isRetryableError,
        operation: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        var lastException: Throwable? = null
        
        repeat(maxRetries + 1) { attempt ->
            try {
                return operation()
            } catch (e: Throwable) {
                lastException = e
                
                // Don't retry on the last attempt or if error is not retryable
                if (attempt == maxRetries || !retryCondition(e)) {
                    throw e
                }
                
                // Calculate delay with exponential backoff and jitter
                val jitter = currentDelay * jitterFactor * Random.nextDouble(-1.0, 1.0)
                val delayWithJitter = (currentDelay + jitter).toLong()
                val actualDelay = min(delayWithJitter, maxDelay)
                
                delay(actualDelay)
                
                // Increase delay for next attempt
                currentDelay = min(
                    (currentDelay * backoffMultiplier).toLong(),
                    maxDelay
                )
            }
        }
        
        throw lastException ?: RuntimeException("Retry operation failed")
    }
    
    /**
     * Creates a Flow with retry logic
     */
    fun <T> createRetryFlow(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        initialDelay: Long = DEFAULT_INITIAL_DELAY,
        maxDelay: Long = DEFAULT_MAX_DELAY,
        backoffMultiplier: Double = DEFAULT_BACKOFF_MULTIPLIER,
        jitterFactor: Double = DEFAULT_JITTER_FACTOR,
        retryCondition: (Throwable) -> Boolean = ::isRetryableError,
        operation: suspend () -> T
    ): Flow<T> = flow {
        emit(
            executeWithRetry(
                maxRetries = maxRetries,
                initialDelay = initialDelay,
                maxDelay = maxDelay,
                backoffMultiplier = backoffMultiplier,
                jitterFactor = jitterFactor,
                retryCondition = retryCondition,
                operation = operation
            )
        )
    }
    
    /**
     * Adds retry logic to an existing Flow
     */
    fun <T> Flow<T>.withRetry(
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        initialDelay: Long = DEFAULT_INITIAL_DELAY,
        maxDelay: Long = DEFAULT_MAX_DELAY,
        backoffMultiplier: Double = DEFAULT_BACKOFF_MULTIPLIER,
        jitterFactor: Double = DEFAULT_JITTER_FACTOR,
        retryCondition: (Throwable) -> Boolean = ::isRetryableError
    ): Flow<T> {
        return this.retry(maxRetries.toLong()) { cause ->
            if (retryCondition(cause)) {
                val attempt = maxRetries - (maxRetries - 1) // Calculate current attempt
                val currentDelay = min(
                    (initialDelay * backoffMultiplier.pow(attempt.toDouble())).toLong(),
                    maxDelay
                )
                val jitter = currentDelay * jitterFactor * Random.nextDouble(-1.0, 1.0)
                val delayWithJitter = (currentDelay + jitter).toLong()
                
                delay(delayWithJitter)
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Determines if an error is retryable
     */
    private fun isRetryableError(throwable: Throwable): Boolean {
        return when (throwable) {
            is NetworkError -> true
            is java.net.SocketTimeoutException -> true
            is java.net.UnknownHostException -> true
            is java.io.IOException -> true
            is retrofit2.HttpException -> {
                // Retry on server errors and rate limiting
                throwable.code() in listOf(408, 429, 500, 502, 503, 504)
            }
            else -> false
        }
    }
    
    /**
     * Creates retry configuration for different operation types
     */
    fun getRetryConfig(operationType: RetryOperationType): RetryConfig {
        return when (operationType) {
            RetryOperationType.CRITICAL -> RetryConfig(
                maxRetries = 5,
                initialDelay = 500L,
                maxDelay = 60000L,
                backoffMultiplier = 2.0,
                jitterFactor = 0.1
            )
            RetryOperationType.NORMAL -> RetryConfig(
                maxRetries = 3,
                initialDelay = 1000L,
                maxDelay = 30000L,
                backoffMultiplier = 2.0,
                jitterFactor = 0.1
            )
            RetryOperationType.BACKGROUND -> RetryConfig(
                maxRetries = 10,
                initialDelay = 2000L,
                maxDelay = 300000L, // 5 minutes
                backoffMultiplier = 1.5,
                jitterFactor = 0.2
            )
            RetryOperationType.QUICK -> RetryConfig(
                maxRetries = 2,
                initialDelay = 500L,
                maxDelay = 5000L,
                backoffMultiplier = 2.0,
                jitterFactor = 0.05
            )
        }
    }
    
    /**
     * Executes operation with predefined retry configuration
     */
    suspend fun <T> executeWithConfig(
        operationType: RetryOperationType,
        retryCondition: (Throwable) -> Boolean = ::isRetryableError,
        operation: suspend () -> T
    ): T {
        val config = getRetryConfig(operationType)
        return executeWithRetry(
            maxRetries = config.maxRetries,
            initialDelay = config.initialDelay,
            maxDelay = config.maxDelay,
            backoffMultiplier = config.backoffMultiplier,
            jitterFactor = config.jitterFactor,
            retryCondition = retryCondition,
            operation = operation
        )
    }
}

/**
 * Retry configuration
 */
data class RetryConfig(
    val maxRetries: Int,
    val initialDelay: Long,
    val maxDelay: Long,
    val backoffMultiplier: Double,
    val jitterFactor: Double
)

/**
 * Types of retry operations
 */
enum class RetryOperationType {
    CRITICAL,    // User-facing operations that must succeed
    NORMAL,      // Standard operations
    BACKGROUND,  // Background sync operations
    QUICK        // Operations that should fail fast
}

/**
 * Retry statistics
 */
data class RetryStats(
    val totalAttempts: Int,
    val successfulRetries: Int,
    val failedRetries: Int,
    val averageRetryDelay: Long,
    val maxRetryDelay: Long
)

/**
 * Extension functions for common retry patterns
 */

/**
 * Retry for authentication operations
 */
suspend fun <T> RetryManager.executeAuthOperation(operation: suspend () -> T): T {
    return executeWithConfig(
        operationType = RetryOperationType.CRITICAL,
        retryCondition = { throwable ->
            // Don't retry authentication errors, but retry network issues
            when (throwable) {
                is AuthenticationError -> false
                else -> isRetryableError(throwable)
            }
        },
        operation = operation
    )
}

/**
 * Retry for data loading operations
 */
suspend fun <T> RetryManager.executeDataOperation(operation: suspend () -> T): T {
    return executeWithConfig(
        operationType = RetryOperationType.NORMAL,
        operation = operation
    )
}

/**
 * Retry for background sync operations
 */
suspend fun <T> RetryManager.executeBackgroundOperation(operation: suspend () -> T): T {
    return executeWithConfig(
        operationType = RetryOperationType.BACKGROUND,
        operation = operation
    )
}

/**
 * Retry for quick operations that should fail fast
 */
suspend fun <T> RetryManager.executeQuickOperation(operation: suspend () -> T): T {
    return executeWithConfig(
        operationType = RetryOperationType.QUICK,
        operation = operation
    )
}

/**
 * Helper function to determine if error is retryable
 */
private fun isRetryableError(throwable: Throwable): Boolean {
    return when (throwable) {
        is NetworkError -> true
        is java.net.SocketTimeoutException -> true
        is java.net.UnknownHostException -> true
        is java.io.IOException -> true
        is retrofit2.HttpException -> {
            throwable.code() in listOf(408, 429, 500, 502, 503, 504)
        }
        else -> false
    }
}