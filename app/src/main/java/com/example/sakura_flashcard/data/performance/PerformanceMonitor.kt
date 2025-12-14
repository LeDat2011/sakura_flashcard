package com.example.sakura_flashcard.data.performance

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitors application performance and provides analytics
 */
@Singleton
class PerformanceMonitor @Inject constructor() {
    
    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()
    
    private val operationTimings = ConcurrentHashMap<String, MutableList<Long>>()
    private val operationCounts = ConcurrentHashMap<String, AtomicLong>()
    private val errorCounts = ConcurrentHashMap<String, AtomicLong>()
    private val activeOperations = ConcurrentHashMap<String, Long>()
    
    /**
     * Starts timing an operation
     */
    fun startOperation(operationName: String): String {
        val operationId = "${operationName}_${System.currentTimeMillis()}_${Thread.currentThread().id}"
        activeOperations[operationId] = SystemClock.elapsedRealtime()
        
        // Increment operation count
        operationCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        
        return operationId
    }
    
    /**
     * Ends timing an operation
     */
    fun endOperation(operationId: String, success: Boolean = true) {
        val startTime = activeOperations.remove(operationId) ?: return
        val endTime = SystemClock.elapsedRealtime()
        val duration = endTime - startTime
        
        // Extract operation name from operation ID
        val operationName = operationId.substringBefore("_")
        
        // Record timing
        operationTimings.computeIfAbsent(operationName) { mutableListOf() }.add(duration)
        
        // Record error if operation failed
        if (!success) {
            errorCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        }
        
        // Update metrics
        updateMetrics()
    }
    
    /**
     * Records an operation with automatic timing
     */
    suspend fun <T> measureOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        val operationId = startOperation(operationName)
        return try {
            val result = operation()
            endOperation(operationId, success = true)
            result
        } catch (e: Exception) {
            endOperation(operationId, success = false)
            throw e
        }
    }
    
    /**
     * Records a memory usage sample
     */
    fun recordMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryUsagePercent = (usedMemory.toDouble() / maxMemory * 100).toFloat()
        
        val currentMetrics = _performanceMetrics.value
        _performanceMetrics.value = currentMetrics.copy(
            memoryUsageMB = usedMemory / (1024 * 1024),
            memoryUsagePercent = memoryUsagePercent,
            maxMemoryMB = maxMemory / (1024 * 1024)
        )
    }
    
    /**
     * Records network operation metrics
     */
    fun recordNetworkOperation(
        operationName: String,
        duration: Long,
        bytesTransferred: Long,
        success: Boolean
    ) {
        operationTimings.computeIfAbsent(operationName) { mutableListOf() }.add(duration)
        operationCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        
        if (!success) {
            errorCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        }
        
        val currentMetrics = _performanceMetrics.value
        _performanceMetrics.value = currentMetrics.copy(
            totalNetworkRequests = currentMetrics.totalNetworkRequests + 1,
            totalBytesTransferred = currentMetrics.totalBytesTransferred + bytesTransferred,
            networkErrorCount = if (!success) currentMetrics.networkErrorCount + 1 else currentMetrics.networkErrorCount
        )
        
        updateMetrics()
    }
    
    /**
     * Records UI operation metrics
     */
    fun recordUIOperation(
        operationName: String,
        renderTime: Long,
        frameDrops: Int = 0
    ) {
        operationTimings.computeIfAbsent(operationName) { mutableListOf() }.add(renderTime)
        operationCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        
        val currentMetrics = _performanceMetrics.value
        _performanceMetrics.value = currentMetrics.copy(
            totalUIOperations = currentMetrics.totalUIOperations + 1,
            totalFrameDrops = currentMetrics.totalFrameDrops + frameDrops,
            averageRenderTime = calculateAverageRenderTime()
        )
        
        updateMetrics()
    }
    
    /**
     * Records database operation metrics
     */
    fun recordDatabaseOperation(
        operationName: String,
        duration: Long,
        recordsAffected: Int,
        success: Boolean
    ) {
        operationTimings.computeIfAbsent(operationName) { mutableListOf() }.add(duration)
        operationCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        
        if (!success) {
            errorCounts.computeIfAbsent(operationName) { AtomicLong(0) }.incrementAndGet()
        }
        
        val currentMetrics = _performanceMetrics.value
        _performanceMetrics.value = currentMetrics.copy(
            totalDatabaseOperations = currentMetrics.totalDatabaseOperations + 1,
            totalRecordsProcessed = currentMetrics.totalRecordsProcessed + recordsAffected,
            databaseErrorCount = if (!success) currentMetrics.databaseErrorCount + 1 else currentMetrics.databaseErrorCount
        )
        
        updateMetrics()
    }
    
    /**
     * Gets performance statistics for a specific operation
     */
    fun getOperationStats(operationName: String): OperationStats? {
        val timings = operationTimings[operationName] ?: return null
        val count = operationCounts[operationName]?.get() ?: 0
        val errors = errorCounts[operationName]?.get() ?: 0
        
        if (timings.isEmpty()) return null
        
        val sortedTimings = timings.sorted()
        return OperationStats(
            operationName = operationName,
            totalCount = count,
            errorCount = errors,
            successRate = if (count > 0) ((count - errors).toDouble() / count * 100).toFloat() else 0f,
            averageDuration = timings.average().toLong(),
            minDuration = sortedTimings.first(),
            maxDuration = sortedTimings.last(),
            p50Duration = sortedTimings[sortedTimings.size / 2],
            p95Duration = sortedTimings[(sortedTimings.size * 0.95).toInt()],
            p99Duration = sortedTimings[(sortedTimings.size * 0.99).toInt()]
        )
    }
    
    /**
     * Gets all operation statistics
     */
    fun getAllOperationStats(): List<OperationStats> {
        return operationTimings.keys.mapNotNull { getOperationStats(it) }
    }
    
    /**
     * Resets all performance metrics
     */
    fun resetMetrics() {
        operationTimings.clear()
        operationCounts.clear()
        errorCounts.clear()
        activeOperations.clear()
        _performanceMetrics.value = PerformanceMetrics()
    }
    
    /**
     * Gets performance summary
     */
    fun getPerformanceSummary(): PerformanceSummary {
        val allStats = getAllOperationStats()
        val currentMetrics = _performanceMetrics.value
        
        return PerformanceSummary(
            totalOperations = allStats.sumOf { it.totalCount },
            totalErrors = allStats.sumOf { it.errorCount },
            averageSuccessRate = if (allStats.isNotEmpty()) {
                allStats.map { it.successRate }.average().toFloat()
            } else 0f,
            slowestOperations = allStats.sortedByDescending { it.averageDuration }.take(5),
            mostErrorProneOperations = allStats.sortedByDescending { it.errorCount }.take(5),
            memoryUsage = currentMetrics.memoryUsageMB,
            networkStats = NetworkStats(
                totalRequests = currentMetrics.totalNetworkRequests,
                errorCount = currentMetrics.networkErrorCount,
                totalBytesTransferred = currentMetrics.totalBytesTransferred,
                averageRequestTime = calculateAverageNetworkTime()
            ),
            uiStats = UIStats(
                totalOperations = currentMetrics.totalUIOperations,
                totalFrameDrops = currentMetrics.totalFrameDrops,
                averageRenderTime = currentMetrics.averageRenderTime
            ),
            databaseStats = DatabaseStats(
                totalOperations = currentMetrics.totalDatabaseOperations,
                errorCount = currentMetrics.databaseErrorCount,
                totalRecordsProcessed = currentMetrics.totalRecordsProcessed,
                averageOperationTime = calculateAverageDatabaseTime()
            )
        )
    }
    
    private fun updateMetrics() {
        recordMemoryUsage()
    }
    
    private fun calculateAverageRenderTime(): Long {
        val uiTimings = operationTimings.filterKeys { it.contains("ui_") || it.contains("render_") }
        val allTimings = uiTimings.values.flatten()
        return if (allTimings.isNotEmpty()) allTimings.average().toLong() else 0L
    }
    
    private fun calculateAverageNetworkTime(): Long {
        val networkTimings = operationTimings.filterKeys { it.contains("network_") || it.contains("api_") }
        val allTimings = networkTimings.values.flatten()
        return if (allTimings.isNotEmpty()) allTimings.average().toLong() else 0L
    }
    
    private fun calculateAverageDatabaseTime(): Long {
        val dbTimings = operationTimings.filterKeys { it.contains("db_") || it.contains("database_") }
        val allTimings = dbTimings.values.flatten()
        return if (allTimings.isNotEmpty()) allTimings.average().toLong() else 0L
    }
}

/**
 * Overall performance metrics
 */
data class PerformanceMetrics(
    val memoryUsageMB: Long = 0,
    val memoryUsagePercent: Float = 0f,
    val maxMemoryMB: Long = 0,
    val totalNetworkRequests: Long = 0,
    val totalBytesTransferred: Long = 0,
    val networkErrorCount: Long = 0,
    val totalUIOperations: Long = 0,
    val totalFrameDrops: Int = 0,
    val averageRenderTime: Long = 0,
    val totalDatabaseOperations: Long = 0,
    val totalRecordsProcessed: Int = 0,
    val databaseErrorCount: Long = 0
)

/**
 * Statistics for a specific operation
 */
data class OperationStats(
    val operationName: String,
    val totalCount: Long,
    val errorCount: Long,
    val successRate: Float,
    val averageDuration: Long,
    val minDuration: Long,
    val maxDuration: Long,
    val p50Duration: Long,
    val p95Duration: Long,
    val p99Duration: Long
)

/**
 * Performance summary
 */
data class PerformanceSummary(
    val totalOperations: Long,
    val totalErrors: Long,
    val averageSuccessRate: Float,
    val slowestOperations: List<OperationStats>,
    val mostErrorProneOperations: List<OperationStats>,
    val memoryUsage: Long,
    val networkStats: NetworkStats,
    val uiStats: UIStats,
    val databaseStats: DatabaseStats
)

/**
 * Network performance statistics
 */
data class NetworkStats(
    val totalRequests: Long,
    val errorCount: Long,
    val totalBytesTransferred: Long,
    val averageRequestTime: Long
)

/**
 * UI performance statistics
 */
data class UIStats(
    val totalOperations: Long,
    val totalFrameDrops: Int,
    val averageRenderTime: Long
)

/**
 * Database performance statistics
 */
data class DatabaseStats(
    val totalOperations: Long,
    val errorCount: Long,
    val totalRecordsProcessed: Int,
    val averageOperationTime: Long
)