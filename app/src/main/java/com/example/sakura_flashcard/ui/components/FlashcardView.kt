package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.Flashcard
import com.example.sakura_flashcard.ui.accessibility.*
import com.example.sakura_flashcard.ui.theme.*

enum class InteractionType {
    FLIP, LEARNED, NOT_LEARNED, PLAY_AUDIO, SKIP
}

@Composable
fun FlashcardView(
    flashcard: Flashcard,
    isFlipped: Boolean = false,
    isLearned: Boolean = false,
    onInteraction: (InteractionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accessibilityState = rememberAccessibilityState()
    val accessibilityService = rememberAccessibilityService()
    val focusRequester = remember { FocusRequester() }
    
    // Get animation duration based on accessibility preferences
    val animationDuration = AccessibilityUtils.getAnimationDuration(context, 600)
    
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(animationDuration),
        label = "cardRotation"
    )
    
    // Create comprehensive content description
    val currentSide = if (isFlipped) flashcard.back else flashcard.front
    val contentDescription = AccessibilityUtils.createJapaneseContentDescription(
        text = currentSide.text,
        pronunciation = null, // FlashcardSide doesn't have pronunciation field
        translation = currentSide.translation
    )
    
    val fullContentDescription = buildString {
        append(if (isFlipped) "Back of flashcard. " else "Front of flashcard. ")
        append(contentDescription)
        append(". ")
        append(AccessibilityUtils.createFlashcardStatusDescription(isLearned))
        append(". Double tap to flip card.")
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .keyboardNavigable(
                focusRequester = focusRequester,
                onFlip = { onInteraction(InteractionType.FLIP) },
                onSelect = { onInteraction(InteractionType.FLIP) },
                onPlayAudio = { onInteraction(InteractionType.PLAY_AUDIO) },
                onMarkLearned = { onInteraction(InteractionType.LEARNED) },
                onMarkNotLearned = { onInteraction(InteractionType.NOT_LEARNED) }
            )
            .focusIndicator()
            .clickable { onInteraction(InteractionType.FLIP) }
            .semantics {
                this.contentDescription = fullContentDescription
                flashcardSide(if (isFlipped) "back" else "front")
                learningStatus(if (isLearned) "learned" else "not learned")
                if (!currentSide.translation.isNullOrBlank()) {
                    japaneseTranslation(currentSide.translation!!)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status indicator
            FlashcardStatusIndicator(
                isLearned = isLearned,
                onStatusChange = { learned ->
                    onInteraction(if (learned) InteractionType.LEARNED else InteractionType.NOT_LEARNED)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
            
            // Card content
            if (rotation <= 90f) {
                // Front side
                FlashcardSide(
                    side = flashcard.front,
                    isBack = false,
                    onAudioPlay = { onInteraction(InteractionType.PLAY_AUDIO) },
                    accessibilityService = accessibilityService,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Back side (flipped)
                FlashcardSide(
                    side = flashcard.back,
                    isBack = true,
                    onAudioPlay = { onInteraction(InteractionType.PLAY_AUDIO) },
                    accessibilityService = accessibilityService,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

@Composable
private fun FlashcardSide(
    side: com.example.sakura_flashcard.data.model.FlashcardSide,
    isBack: Boolean,
    onAudioPlay: () -> Unit,
    accessibilityService: AccessibilityService,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main text
        Text(
            text = side.text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Japanese text: ${side.text}"
                }
        )
        
        // Translation (if available)
        if (side.hasTranslation()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = side.translation!!,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Explanation (if available)
        if (side.hasExplanation()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = side.explanation!!,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Audio button (if available) or TTS button
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (side.hasAudio()) {
                IconButton(
                    onClick = onAudioPlay,
                    modifier = Modifier
                        .size(40.dp)
                        .accessibleTouchTarget()
                        .semantics {
                            contentDescription = "Play audio pronunciation"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // TTS button for Japanese pronunciation - always show button
            IconButton(
                onClick = {
                    accessibilityService.speakJapanese(side.text)
                },
                modifier = Modifier
                    .size(40.dp)
                    .accessibleTouchTarget()
                    .semantics {
                        contentDescription = "Speak flashcard content with Japanese pronunciation"
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun FlashcardStatusIndicator(
    isLearned: Boolean,
    onStatusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentDescription = AccessibilityUtils.createFlashcardStatusDescription(isLearned)
    
    IconButton(
        onClick = { onStatusChange(!isLearned) },
        modifier = modifier
            .size(48.dp) // Increased for better touch target
            .accessibleTouchTarget()
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        Icon(
            imageVector = if (isLearned) Icons.Default.CheckCircle else Icons.Default.Add,
            contentDescription = null, // Using semantics instead
            tint = if (isLearned) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}