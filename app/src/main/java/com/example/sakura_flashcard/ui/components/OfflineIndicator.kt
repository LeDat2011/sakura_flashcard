package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.sync.SyncState

/**
 * UI component that shows offline status and sync information
 */
@Composable
fun OfflineIndicator(
    isOnline: Boolean,
    syncState: SyncState,
    onSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isOnline || syncState.pendingOperations > 0 || syncState.lastError != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    !isOnline -> MaterialTheme.colorScheme.errorContainer
                    syncState.lastError != null -> MaterialTheme.colorScheme.errorContainer
                    syncState.isSyncing -> MaterialTheme.colorScheme.primaryContainer
                    syncState.pendingOperations > 0 -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when {
                            !isOnline -> Icons.Default.Warning
                            syncState.lastError != null -> Icons.Default.Warning
                            syncState.isSyncing -> Icons.Default.Refresh
                            else -> Icons.Default.Refresh
                        },
                        contentDescription = null,
                        tint = when {
                            !isOnline -> MaterialTheme.colorScheme.onErrorContainer
                            syncState.lastError != null -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = when {
                                !isOnline -> "Chế độ ngoại tuyến"
                                syncState.isSyncing -> "Đang đồng bộ..."
                                syncState.lastError != null -> "Lỗi đồng bộ"
                                syncState.pendingOperations > 0 -> "Đang chờ đồng bộ"
                                else -> "Sẵn sàng đồng bộ"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                !isOnline -> MaterialTheme.colorScheme.onErrorContainer
                                syncState.lastError != null -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        
                        if (syncState.pendingOperations > 0) {
                            Text(
                                text = "${syncState.pendingOperations} thao tác đang chờ",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        
                        if (syncState.lastError != null) {
                            Text(
                                text = syncState.lastError,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }
                }
                
                if (isOnline && !syncState.isSyncing && syncState.pendingOperations > 0) {
                    TextButton(
                        onClick = onSyncClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Đồng bộ ngay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact version of the offline indicator for use in app bars
 */
@Composable
fun CompactOfflineIndicator(
    isOnline: Boolean,
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isOnline || syncState.pendingOperations > 0,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        !isOnline -> MaterialTheme.colorScheme.errorContainer
                        syncState.isSyncing -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = when {
                    !isOnline -> Icons.Default.Warning
                    syncState.isSyncing -> Icons.Default.Refresh
                    else -> Icons.Default.Refresh
                },
                contentDescription = null,
                tint = when {
                    !isOnline -> MaterialTheme.colorScheme.onErrorContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(16.dp)
            )
            
            if (syncState.pendingOperations > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = syncState.pendingOperations.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        !isOnline -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

/**
 * Sync status badge for showing detailed sync information
 */
@Composable
fun SyncStatusBadge(
    syncState: SyncState,
    modifier: Modifier = Modifier
) {
    if (syncState.lastSyncTime != null || syncState.pendingOperations > 0) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Trạng thái đồng bộ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (syncState.lastSyncTime != null) {
                    Text(
                        text = "Lần đồng bộ cuối: ${formatSyncTime(syncState.lastSyncTime)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                if (syncState.pendingOperations > 0) {
                    Text(
                        text = "${syncState.pendingOperations} thao tác đang chờ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                if (syncState.isSyncing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đang đồng bộ...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formats sync time for display
 */
private fun formatSyncTime(syncTime: java.time.Instant): String {
    val now = java.time.Instant.now()
    val duration = java.time.Duration.between(syncTime, now)
    
    return when {
        duration.toMinutes() < 1 -> "Vừa xong"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} phút trước"
        duration.toHours() < 24 -> "${duration.toHours()} giờ trước"
        else -> "${duration.toDays()} ngày trước"
    }
}