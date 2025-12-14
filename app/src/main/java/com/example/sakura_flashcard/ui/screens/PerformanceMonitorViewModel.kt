package com.example.sakura_flashcard.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakura_flashcard.data.performance.ImageCacheStats
import com.example.sakura_flashcard.data.performance.PerformanceMonitor
import com.example.sakura_flashcard.data.performance.PerformanceSummary
import com.example.sakura_flashcard.data.repository.EnhancedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for performance monitoring screen
 */
@HiltViewModel
class PerformanceMonitorViewModel @Inject constructor(
    private val performanceMonitor: PerformanceMonitor,
    private val enhancedRepository: EnhancedRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PerformanceMonitorUiState())
    val uiState: StateFlow<PerformanceMonitorUiState> = _uiState.asStateFlow()
    
    init {
        refreshStats()
    }
    
    /**
     * Refreshes performance statistics
     */
    fun refreshStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val performanceSummary = enhancedRepository.getPerformanceStats()
                val imageCacheStats = enhancedRepository.getImageCacheStats()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    performanceSummary = performanceSummary,
                    imageCacheStats = imageCacheStats,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load performance stats"
                )
            }
        }
    }
    
    /**
     * Clears performance statistics
     */
    fun clearStats() {
        viewModelScope.launch {
            try {
                performanceMonitor.resetMetrics()
                refreshStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to clear stats"
                )
            }
        }
    }
    
    /**
     * Clears caches
     */
    fun clearCaches() {
        viewModelScope.launch {
            try {
                enhancedRepository.clearCaches()
                refreshStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to clear caches"
                )
            }
        }
    }
    
    /**
     * Clears error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * UI state for performance monitor screen
 */
data class PerformanceMonitorUiState(
    val isLoading: Boolean = false,
    val performanceSummary: PerformanceSummary = PerformanceSummary(
        totalOperations = 0,
        totalErrors = 0,
        averageSuccessRate = 0f,
        slowestOperations = emptyList(),
        mostErrorProneOperations = emptyList(),
        memoryUsage = 0,
        networkStats = com.example.sakura_flashcard.data.performance.NetworkStats(0, 0, 0, 0),
        uiStats = com.example.sakura_flashcard.data.performance.UIStats(0, 0, 0),
        databaseStats = com.example.sakura_flashcard.data.performance.DatabaseStats(0, 0, 0, 0)
    ),
    val imageCacheStats: ImageCacheStats = ImageCacheStats(0, 0, 0, 0, 0, 0),
    val error: String? = null
)