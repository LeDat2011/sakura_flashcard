package com.example.sakura_flashcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sakura_flashcard.data.error.AppError
import com.example.sakura_flashcard.data.error.ErrorSeverity
import com.example.sakura_flashcard.data.error.ErrorType
import kotlinx.coroutines.launch

/**
 * Displays error messages with retry functionality
 */
@Composable
fun ErrorDisplay(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isRetrying by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (error.severity) {
                ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
                ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                ErrorSeverity.LOW -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = getErrorIcon(error.type, error.severity),
                    contentDescription = null,
                    tint = when (error.severity) {
                        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.error
                        ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.secondary
                        ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                Text(
                    text = getErrorTitle(error.type),
                    style = MaterialTheme.typography.titleMedium,
                    color = when (error.severity) {
                        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer
                        ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                        ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Text(
                text = error.message,
                style = MaterialTheme.typography.bodyMedium,
                color = when (error.severity) {
                    ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> MaterialTheme.colorScheme.onErrorContainer
                    ErrorSeverity.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                    ErrorSeverity.LOW -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                onDismiss?.let {
                    TextButton(onClick = it) {
                        Text("Bỏ qua")
                    }
                }
                
                if (error.isRetryable && (onRetry != null || error.retryAction != null)) {
                    Button(
                        onClick = {
                            scope.launch {
                                isRetrying = true
                                try {
                                    if (onRetry != null) {
                                        onRetry()
                                    } else {
                                        error.retryAction?.invoke()
                                    }
                                } finally {
                                    isRetrying = false
                                }
                            }
                        },
                        enabled = !isRetrying
                    ) {
                        if (isRetrying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRetrying) "Đang thử lại..." else "Thử lại")
                    }
                }
            }
        }
    }
}

/**
 * Compact error display for inline use
 */
@Composable
fun CompactErrorDisplay(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isRetrying by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = getErrorIcon(error.type, error.severity),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = error.message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        if (error.isRetryable && (onRetry != null || error.retryAction != null)) {
            IconButton(
                onClick = {
                    scope.launch {
                        isRetrying = true
                        try {
                            if (onRetry != null) {
                                onRetry()
                            } else {
                                error.retryAction?.invoke()
                            }
                        } finally {
                            isRetrying = false
                        }
                    }
                },
                enabled = !isRetrying
            ) {
                if (isRetrying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Thử lại",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Error snackbar with retry action
 */
@Composable
fun ErrorSnackbar(
    error: AppError,
    onRetry: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isRetrying by remember { mutableStateOf(false) }
    
    Snackbar(
        modifier = modifier,
        action = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (error.isRetryable && (onRetry != null || error.retryAction != null)) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isRetrying = true
                                try {
                                    if (onRetry != null) {
                                        onRetry()
                                    } else {
                                        error.retryAction?.invoke()
                                    }
                                } finally {
                                    isRetrying = false
                                }
                            }
                        },
                        enabled = !isRetrying
                    ) {
                        Text(if (isRetrying) "Đang thử lại..." else "Thử lại")
                    }
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Bỏ qua")
                }
            }
        }
    ) {
        Text(
            text = error.message,
            maxLines = 2
        )
    }
}

/**
 * Loading state with error fallback
 */
@Composable
fun LoadingWithError(
    isLoading: Boolean,
    error: AppError?,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = getErrorIcon(error.type, error.severity),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Text(
                        text = error.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (error.isRetryable && (onRetry != null || error.retryAction != null)) {
                        Button(
                            onClick = {
                                if (onRetry != null) {
                                    onRetry()
                                } else {
                                    error.retryAction?.let { 
                                        kotlinx.coroutines.runBlocking { it() }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thử lại")
                        }
                    }
                }
            }
            else -> {
                content()
            }
        }
    }
}

/**
 * Gets appropriate icon for error type and severity
 */
private fun getErrorIcon(type: ErrorType, severity: ErrorSeverity): ImageVector {
    return when (severity) {
        ErrorSeverity.HIGH, ErrorSeverity.CRITICAL -> Icons.Default.Error
        else -> Icons.Default.Warning
    }
}

/**
 * Gets appropriate title for error type
 */
private fun getErrorTitle(type: ErrorType): String {
    return when (type) {
        ErrorType.NETWORK -> "Lỗi kết nối"
        ErrorType.AUTHENTICATION -> "Lỗi xác thực"
        ErrorType.VALIDATION -> "Lỗi xác nhận"
        ErrorType.DATA -> "Lỗi dữ liệu"
        ErrorType.UNKNOWN -> "Lỗi không xác định"
    }
}