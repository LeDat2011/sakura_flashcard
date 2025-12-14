package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

/**
 * Unified Flashcard Component - Consistent design across the app
 */

// Flashcard dimensions and colors
private object FlashcardStyle {
    val cardHeight = 280.dp
    val cardCornerRadius = 28.dp
    val cardPadding = 28.dp
    
    // Soft pastel gradients
    val frontGradient = listOf(
        Color(0xFFf0f9ff),  // sky-50
        Color(0xFFe0f2fe)   // sky-100
    )
    val backGradient = listOf(
        Color(0xFFfdf2f8),  // pink-50
        Color(0xFFfce7f3)   // pink-100
    )
    val learnedGradient = listOf(
        Color(0xFFecfdf5),  // emerald-50
        Color(0xFFd1fae5)   // emerald-200
    )
}

@Composable
fun UnifiedFlashcard(
    frontTitle: String,
    frontSubtitle: String? = null,
    backTitle: String,
    backSubtitle: String? = null,
    isFlipped: Boolean,
    isLearned: Boolean = false,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "flip"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(FlashcardStyle.cardHeight)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(FlashcardStyle.cardCornerRadius),
                ambientColor = Color(0xFF64748b).copy(alpha = 0.1f),
                spotColor = Color(0xFF64748b).copy(alpha = 0.15f)
            )
            .clickable { onFlip() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(FlashcardStyle.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = when {
                            isLearned -> FlashcardStyle.learnedGradient
                            rotation <= 90f -> FlashcardStyle.frontGradient
                            else -> FlashcardStyle.backGradient
                        }
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Front content
                FlashcardContent(
                    title = frontTitle,
                    subtitle = frontSubtitle,
                    hint = "Nhấn để lật thẻ",
                    accentColor = AppColors.PrimaryLight,
                    isLearned = isLearned
                )
            } else {
                // Back content (mirrored)
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    FlashcardContent(
                        title = backTitle,
                        subtitle = backSubtitle,
                        hint = "Nhấn để xem mặt trước",
                        accentColor = AppColors.SecondaryLight,
                        isLearned = isLearned
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardContent(
    title: String,
    subtitle: String?,
    hint: String,
    accentColor: Color,
    isLearned: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(FlashcardStyle.cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Learned badge
        if (isLearned) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppColors.SuccessLight.copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = AppColors.SuccessLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Đã học",
                        style = AppTypography.LabelSmall,
                        color = AppColors.SuccessLight
                    )
                }
            }
        }

        // Main title - TĂNG SIZE
        Text(
            text = title,
            style = AppTypography.HeadlineLarge.copy(
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 48.sp
            ),
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        // Subtitle
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = subtitle,
                style = AppTypography.TitleLarge,
                color = accentColor,
                textAlign = TextAlign.Center
            )
        }

        // Hint - đặt ở dưới cùng
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = hint,
            style = AppTypography.BodySmall,
            color = AppColors.TextTertiary
        )
    }
}

/**
 * Flashcard with navigation controls
 */
@Composable
fun UnifiedFlashcardWithControls(
    frontTitle: String,
    frontSubtitle: String? = null,
    backTitle: String,
    backSubtitle: String? = null,
    isFlipped: Boolean,
    isLearned: Boolean = false,
    currentIndex: Int,
    totalCount: Int,
    onFlip: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onMarkLearned: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Thẻ ${currentIndex + 1}/$totalCount",
                style = AppTypography.TitleMedium,
                color = AppColors.TextPrimary
            )
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalCount },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = AppColors.PrimaryLight,
            trackColor = AppColors.PrimaryLightest,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Flashcard
        UnifiedFlashcard(
            frontTitle = frontTitle,
            frontSubtitle = frontSubtitle,
            backTitle = backTitle,
            backSubtitle = backSubtitle,
            isFlipped = isFlipped,
            isLearned = isLearned,
            onFlip = onFlip
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            NavigationButton(
                icon = Icons.Default.ChevronLeft,
                label = "Trước",
                enabled = currentIndex > 0,
                onClick = onPrevious,
                containerColor = AppColors.SurfaceMedium,
                contentColor = AppColors.TextPrimary
            )

            // Mark learned button (optional)
            if (onMarkLearned != null) {
                NavigationButton(
                    icon = Icons.Default.Check,
                    label = if (isLearned) "Đã học" else "Đánh dấu",
                    enabled = true,
                    onClick = onMarkLearned,
                    containerColor = if (isLearned) AppColors.SuccessLighter else AppColors.SuccessLightest,
                    contentColor = AppColors.SuccessLight
                )
            }

            // Next button
            NavigationButton(
                icon = Icons.Default.ChevronRight,
                label = "Tiếp",
                enabled = currentIndex < totalCount - 1,
                onClick = onNext,
                containerColor = AppColors.PrimaryLightest,
                contentColor = AppColors.PrimaryLight
            )
        }
    }
}

@Composable
private fun NavigationButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor.copy(alpha = 0.5f),
                disabledContentColor = contentColor.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = AppTypography.LabelSmall,
            color = if (enabled) AppColors.TextSecondary else AppColors.TextDisabled
        )
    }
}
