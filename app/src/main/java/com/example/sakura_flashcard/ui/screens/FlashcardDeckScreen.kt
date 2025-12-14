package com.example.sakura_flashcard.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.VolumeUp
import com.example.sakura_flashcard.ui.components.UnifiedFlashcardWithControls
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardDeckScreen(
    topic: String,
    level: String,
    onNavigateBack: () -> Unit,
    viewModel: FlashcardDeckViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Initialize TTS
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPANESE
            }
        }
        onDispose { tts?.shutdown() }
    }
    
    // Load flashcards
    LaunchedEffect(topic, level) {
        viewModel.loadFlashcards(topic, level)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.topic?.displayName ?: topic,
                            style = AppTypography.TitleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = uiState.level?.displayName ?: level,
                            style = AppTypography.BodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Rounded.ArrowBack, 
                            contentDescription = "Back",
                            tint = AppColors.PrimaryLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.SurfaceMedium,
                    titleContentColor = AppColors.TextPrimary
                )
            )
        },
        containerColor = AppColors.SurfaceMedium
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.flashcards.isEmpty() -> {
                    EmptyFlashcardState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    FlashcardContent(
                        uiState = uiState,
                        onFlip = { viewModel.flipCard() },
                        onNext = { viewModel.nextCard() },
                        onPrevious = { viewModel.previousCard() },
                        onMarkLearned = { viewModel.markAsLearned(it) },
                        onMarkNotLearned = { viewModel.markAsNotLearned(it) },
                        onSpeak = { text -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFlashcardState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("\uD83D\uDCDA", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Khong co flashcard",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary
        )
        Text(
            text = "Cap do nay sap co!",
            style = AppTypography.BodyMedium,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
private fun FlashcardContent(
    uiState: FlashcardDeckUiState,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkLearned: (String) -> Unit,
    onMarkNotLearned: (String) -> Unit,
    onSpeak: (String) -> Unit
) {
    val currentCard = uiState.flashcards[uiState.currentIndex]
    val isLearned = uiState.learnedCards.contains(currentCard.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress section
        ProgressSection(
            currentIndex = uiState.currentIndex,
            totalCards = uiState.flashcards.size,
            learnedCount = uiState.learnedCards.size
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Unified Flashcard
        UnifiedFlashcardWithControls(
            frontTitle = currentCard.front.text,
            frontSubtitle = currentCard.front.translation,
            backTitle = currentCard.back.text,
            backSubtitle = currentCard.back.translation,
            isFlipped = uiState.isFlipped,
            isLearned = isLearned,
            currentIndex = uiState.currentIndex,
            totalCount = uiState.flashcards.size,
            onFlip = onFlip,
            onPrevious = onPrevious,
            onNext = onNext,
            onMarkLearned = {
                if (isLearned) {
                    onMarkNotLearned(currentCard.id)
                } else {
                    onMarkLearned(currentCard.id)
                }
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TTS Button
        SpeakButton(
            onClick = { onSpeak(currentCard.front.text) }
        )
    }
}

@Composable
private fun SpeakButton(onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppColors.PrimaryLightest,
            contentColor = AppColors.PrimaryLight
        )
    ) {
        Icon(
            Icons.Default.VolumeUp,
            contentDescription = "Phát âm",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Phát âm", style = AppTypography.LabelSmall)
    }
}

@Composable
private fun ProgressSection(
    currentIndex: Int,
    totalCards: Int,
    learnedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.SurfaceLight
        ),
        border = BorderStroke(1.dp, AppColors.SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card counter with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Style,
                        contentDescription = null,
                        tint = AppColors.PrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${currentIndex + 1} / $totalCards",
                        style = AppTypography.TitleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
                
                // Learned counter with badge
                Surface(
                    color = if (learnedCount > 0) AppColors.SuccessLight.copy(alpha = 0.2f) 
                           else AppColors.SurfaceBorder,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = if (learnedCount > 0) AppColors.SuccessLight else AppColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$learnedCount learned",
                            style = AppTypography.LabelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (learnedCount > 0) AppColors.SuccessLight else AppColors.TextSecondary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Animated progress bar
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalCards },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = AppColors.PrimaryLight,
                trackColor = AppColors.PrimaryLightest
            )
        }
    }
}


