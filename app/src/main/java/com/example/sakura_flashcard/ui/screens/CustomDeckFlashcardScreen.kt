package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.ui.components.UnifiedFlashcardWithControls
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun CustomDeckFlashcardScreen(
    deckId: String,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val customDecks by viewModel.customDecks.collectAsState()
    val deck = customDecks.find { it.id == deckId }

    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    if (deck == null || deck.flashcards.isEmpty()) {
        EmptyDeckScreen(onNavigateBack = onNavigateBack)
        return
    }

    val currentCard = deck.flashcards[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = AppColors.TextPrimary
                )
            }

            Text(
                text = deck.name,
                style = AppTypography.TitleLarge,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Unified Flashcard with controls
        UnifiedFlashcardWithControls(
            frontTitle = currentCard.japanese,
            frontSubtitle = currentCard.romaji,
            backTitle = currentCard.vietnamese,
            backSubtitle = currentCard.romaji,
            isFlipped = isFlipped,
            isLearned = false,
            currentIndex = currentIndex,
            totalCount = deck.flashcards.size,
            onFlip = { isFlipped = !isFlipped },
            onPrevious = {
                if (currentIndex > 0) {
                    currentIndex--
                    isFlipped = false
                }
            },
            onNext = {
                if (currentIndex < deck.flashcards.size - 1) {
                    currentIndex++
                    isFlipped = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmptyDeckScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Không tìm thấy bộ thẻ hoặc bộ thẻ trống",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateBack,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
        ) {
            Text("Quay lại")
        }
    }
}
