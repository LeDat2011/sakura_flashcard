package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.performance.OperationStats
import com.example.sakura_flashcard.data.performance.PerformanceSummary

/**
 * Screen for monitoring application performance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceMonitorScreen(
    onNavigateBack: () -> Unit,
    viewModel: PerformanceMonitorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance Monitor") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshStats() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { viewModel.clearStats() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PerformanceSummaryCard(summary = uiState.performanceSummary)
            }
            
            item {
                MemoryUsageCard(
                    memoryUsageMB = uiState.performanceSummary.memoryUsage,
                    imageCacheStats = uiState.imageCacheStats
                )
            }
            
            item {
                NetworkStatsCard(networkStats = uiState.performanceSummary.networkStats)
            }
            
            item {
                UIStatsCard(uiStats = uiState.performanceSummary.uiStats)
            }
            
            item {
                DatabaseStatsCard(databaseStats = uiState.performanceSummary.databaseStats)
            }
            
            item {
                Text(
                    text = "Operation Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(uiState.performanceSummary.slowestOperations) { operation ->
                OperationStatsCard(operation = operation)
            }
            
            if (uiState.performanceSummary.mostErrorProneOperations.isNotEmpty()) {
                item {
                    Text(
                        text = "Error-Prone Operations",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                items(uiState.performanceSummary.mostErrorProneOperations) { operation ->
                    OperationStatsCard(
                        operation = operation,
                        highlightErrors = true
                    )
                }
            }
        }
    }
}

@Composable
private fun PerformanceSummaryCard(
    summary: PerformanceSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Performance Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Operations",
                    value = summary.totalOperations.toString()
                )
                StatItem(
                    label = "Total Errors",
                    value = summary.totalErrors.toString(),
                    valueColor = if (summary.totalErrors > 0) MaterialTheme.colorScheme.error else null
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Success Rate",
                    value = "${summary.averageSuccessRate.toInt()}%",
                    valueColor = when {
                        summary.averageSuccessRate >= 95 -> MaterialTheme.colorScheme.primary
                        summary.averageSuccessRate >= 85 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
                StatItem(
                    label = "Memory Usage",
                    value = "${summary.memoryUsage} MB"
                )
            }
        }
    }
}

@Composable
private fun MemoryUsageCard(
    memoryUsageMB: Long,
    imageCacheStats: com.example.sakura_flashcard.data.performance.ImageCacheStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Memory & Cache",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "App Memory",
                    value = "$memoryUsageMB MB"
                )
                StatItem(
                    label = "Image Cache",
                    value = "${imageCacheStats.diskCacheSizeMB.toInt()} MB"
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Cache Hit Rate",
                    value = "${(imageCacheStats.memoryCacheHitRate * 100).toInt()}%"
                )
                StatItem(
                    label = "Cached Images",
                    value = imageCacheStats.diskCacheCount.toString()
                )
            }
        }
    }
}

@Composable
private fun NetworkStatsCard(
    networkStats: com.example.sakura_flashcard.data.performance.NetworkStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Network Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Requests",
                    value = networkStats.totalRequests.toString()
                )
                StatItem(
                    label = "Network Errors",
                    value = networkStats.errorCount.toString(),
                    valueColor = if (networkStats.errorCount > 0) MaterialTheme.colorScheme.error else null
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Avg Request Time",
                    value = "${networkStats.averageRequestTime}ms"
                )
                StatItem(
                    label = "Data Transferred",
                    value = "${networkStats.totalBytesTransferred / 1024} KB"
                )
            }
        }
    }
}

@Composable
private fun UIStatsCard(
    uiStats: com.example.sakura_flashcard.data.performance.UIStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "UI Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "UI Operations",
                    value = uiStats.totalOperations.toString()
                )
                StatItem(
                    label = "Frame Drops",
                    value = uiStats.totalFrameDrops.toString(),
                    valueColor = if (uiStats.totalFrameDrops > 0) MaterialTheme.colorScheme.error else null
                )
            }
            
            StatItem(
                label = "Avg Render Time",
                value = "${uiStats.averageRenderTime}ms"
            )
        }
    }
}

@Composable
private fun DatabaseStatsCard(
    databaseStats: com.example.sakura_flashcard.data.performance.DatabaseStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Database Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "DB Operations",
                    value = databaseStats.totalOperations.toString()
                )
                StatItem(
                    label = "DB Errors",
                    value = databaseStats.errorCount.toString(),
                    valueColor = if (databaseStats.errorCount > 0) MaterialTheme.colorScheme.error else null
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Records Processed",
                    value = databaseStats.totalRecordsProcessed.toString()
                )
                StatItem(
                    label = "Avg Operation Time",
                    value = "${databaseStats.averageOperationTime}ms"
                )
            }
        }
    }
}

@Composable
private fun OperationStatsCard(
    operation: OperationStats,
    highlightErrors: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = if (highlightErrors) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = operation.operationName.replace("_", " ").uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Count",
                    value = operation.totalCount.toString(),
                    compact = true
                )
                StatItem(
                    label = "Errors",
                    value = operation.errorCount.toString(),
                    valueColor = if (operation.errorCount > 0) MaterialTheme.colorScheme.error else null,
                    compact = true
                )
                StatItem(
                    label = "Success Rate",
                    value = "${operation.successRate.toInt()}%",
                    compact = true
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Avg",
                    value = "${operation.averageDuration}ms",
                    compact = true
                )
                StatItem(
                    label = "P95",
                    value = "${operation.p95Duration}ms",
                    compact = true
                )
                StatItem(
                    label = "Max",
                    value = "${operation.maxDuration}ms",
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color? = null,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (compact) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(
            text = label,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}