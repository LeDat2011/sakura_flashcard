package com.example.sakura_flashcard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import com.example.sakura_flashcard.ui.components.InteractionType



@Composable
fun FlashcardCarousel(
    flashcards: List<Flashcard>,
    onCardInteraction: (Flashcard, InteractionType) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔄",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ôn tập ngắt quãng",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }
            
            if (flashcards.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.PrimaryLightest
                ) {
                    Text(
                        text = "${currentIndex + 1}/${flashcards.size}",
                        style = AppTypography.LabelSmall,
                        color = AppColors.PrimaryLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (flashcards.isEmpty()) {
            // Empty state
            EmptySpacedRepetitionState()
        } else {
            val currentCard = flashcards[currentIndex]
            
            // Single Flashcard with unified style
            SpacedRepetitionCard(
                flashcard = currentCard,
                isFlipped = isFlipped,
                onFlip = { 
                    isFlipped = !isFlipped
                    onCardInteraction(currentCard, InteractionType.FLIP)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous
                FilledTonalIconButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                            isFlipped = false
                        }
                    },
                    enabled = currentIndex > 0,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = AppColors.SurfaceMedium
                    )
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Trước")
                }
                
                // Mark as learned
                FilledTonalButton(
                    onClick = { 
                        onCardInteraction(currentCard, InteractionType.LEARNED)
                        if (currentIndex < flashcards.size - 1) {
                            currentIndex++
                            isFlipped = false
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppColors.SuccessLightest,
                        contentColor = AppColors.SuccessLight
                    )
                ) {
                    Text("Đã nhớ", style = AppTypography.LabelSmall)
                }
                
                // Skip
                FilledTonalButton(
                    onClick = { 
                        onCardInteraction(currentCard, InteractionType.SKIP)
                        if (currentIndex < flashcards.size - 1) {
                            currentIndex++
                            isFlipped = false
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppColors.WarningLightest,
                        contentColor = AppColors.WarningLight
                    )
                ) {
                    Text("Bỏ qua", style = AppTypography.LabelSmall)
                }
                
                // Next
                FilledTonalIconButton(
                    onClick = {
                        if (currentIndex < flashcards.size - 1) {
                            currentIndex++
                            isFlipped = false
                        }
                    },
                    enabled = currentIndex < flashcards.size - 1,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = AppColors.SurfaceMedium
                    )
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Tiếp")
                }
            }
        }
    }
}

@Composable
private fun SpacedRepetitionCard(
    flashcard: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val frontGradient = listOf(Color(0xFFfdf4ff), Color(0xFFfae8ff)) // purple soft
    val backGradient = listOf(Color(0xFFfff7ed), Color(0xFFffedd5)) // orange soft
    
    Card(
        modifier = modifier
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onFlip
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isFlipped) backGradient else frontGradient
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                if (!isFlipped) {
                    // Front - Japanese text
                    Text(
                        text = flashcard.front.text,
                        style = AppTypography.HeadlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    flashcard.front.translation?.let { translation ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = translation,
                            style = AppTypography.TitleMedium,
                            color = AppColors.CategoryPurple,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Back - Meaning
                    Text(
                        text = flashcard.back.text,
                        style = AppTypography.HeadlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    flashcard.back.translation?.let { translation ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = translation,
                            style = AppTypography.TitleMedium,
                            color = AppColors.CategoryOrange,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isFlipped) "Nhấn để xem mặt trước" else "Nhấn để lật thẻ",
                    style = AppTypography.BodySmall,
                    color = AppColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun EmptySpacedRepetitionState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉",
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tuyệt vời! Không có thẻ cần ôn tập",
                    style = AppTypography.TitleMedium,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Học thêm để có thẻ mới nhé",
                    style = AppTypography.BodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

// Keep FlashcardState for compatibility
data class FlashcardState(
    val flashcard: Flashcard,
    var isFlipped: Boolean = false,
    var isLearned: Boolean = false
)