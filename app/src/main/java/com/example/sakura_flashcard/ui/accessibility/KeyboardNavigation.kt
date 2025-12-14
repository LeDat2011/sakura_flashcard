package com.example.sakura_flashcard.ui.accessibility

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

/**
 * Keyboard navigation utilities for accessibility
 */
object KeyboardNavigation {
    
    /**
     * Common keyboard shortcuts for the application
     */
    object Shortcuts {
        val FLIP_FLASHCARD = Key.Spacebar
        val NEXT_ITEM = Key.DirectionRight
        val PREVIOUS_ITEM = Key.DirectionLeft
        val SELECT_ITEM = Key.Enter
        val ESCAPE = Key.Escape
        val TAB = Key.Tab
        val PLAY_AUDIO = Key.P
        val MARK_LEARNED = Key.L
        val MARK_NOT_LEARNED = Key.N
    }
    
    /**
     * Handles common keyboard navigation patterns
     */
    fun handleKeyEvent(
        event: KeyEvent,
        onFlip: (() -> Unit)? = null,
        onNext: (() -> Unit)? = null,
        onPrevious: (() -> Unit)? = null,
        onSelect: (() -> Unit)? = null,
        onPlayAudio: (() -> Unit)? = null,
        onMarkLearned: (() -> Unit)? = null,
        onMarkNotLearned: (() -> Unit)? = null,
        onEscape: (() -> Unit)? = null
    ): Boolean {
        if (event.type != KeyEventType.KeyDown) return false
        
        return when (event.key) {
            Shortcuts.FLIP_FLASHCARD -> {
                onFlip?.invoke()
                true
            }
            Shortcuts.NEXT_ITEM -> {
                onNext?.invoke()
                true
            }
            Shortcuts.PREVIOUS_ITEM -> {
                onPrevious?.invoke()
                true
            }
            Shortcuts.SELECT_ITEM -> {
                onSelect?.invoke()
                true
            }
            Shortcuts.PLAY_AUDIO -> {
                if (event.isCtrlPressed) {
                    onPlayAudio?.invoke()
                    true
                } else false
            }
            Shortcuts.MARK_LEARNED -> {
                if (event.isCtrlPressed) {
                    onMarkLearned?.invoke()
                    true
                } else false
            }
            Shortcuts.MARK_NOT_LEARNED -> {
                if (event.isCtrlPressed) {
                    onMarkNotLearned?.invoke()
                    true
                } else false
            }
            Shortcuts.ESCAPE -> {
                onEscape?.invoke()
                true
            }
            else -> false
        }
    }
}

/**
 * Modifier that adds keyboard navigation support to a composable
 */
@Composable
fun Modifier.keyboardNavigable(
    focusRequester: FocusRequester = remember { FocusRequester() },
    onFlip: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    onPrevious: (() -> Unit)? = null,
    onSelect: (() -> Unit)? = null,
    onPlayAudio: (() -> Unit)? = null,
    onMarkLearned: (() -> Unit)? = null,
    onMarkNotLearned: (() -> Unit)? = null,
    onEscape: (() -> Unit)? = null
): Modifier {
    return this
        .focusRequester(focusRequester)
        .focusable()
        .onKeyEvent { event ->
            KeyboardNavigation.handleKeyEvent(
                event = event,
                onFlip = onFlip,
                onNext = onNext,
                onPrevious = onPrevious,
                onSelect = onSelect,
                onPlayAudio = onPlayAudio,
                onMarkLearned = onMarkLearned,
                onMarkNotLearned = onMarkNotLearned,
                onEscape = onEscape
            )
        }
}

/**
 * Modifier that adds visible focus indication for keyboard navigation
 */
@Composable
fun Modifier.focusIndicator(
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Float = 2.dp.value
): Modifier {
    var isFocused by remember { mutableStateOf(false) }
    
    return this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        }
        .drawBehind {
            if (isFocused) {
                drawRect(
                    color = color,
                    style = Stroke(width = strokeWidth * density)
                )
            }
        }
}

/**
 * Composable that provides keyboard navigation instructions
 */
@Composable
fun KeyboardNavigationInstructions(
    modifier: Modifier = Modifier,
    showFlashcardControls: Boolean = false,
    showQuizControls: Boolean = false,
    showGameControls: Boolean = false
) {
    // This would typically be a help overlay or instructions panel
    // For now, we'll create a simple invisible helper that announces shortcuts
    val accessibilityService = rememberAccessibilityService()
    
    LaunchedEffect(Unit) {
        if (showFlashcardControls) {
            val instructions = buildString {
                append("Keyboard shortcuts available: ")
                append("Space to flip flashcard, ")
                append("Left and Right arrows to navigate, ")
                append("Enter to select, ")
                append("Ctrl+P to play audio, ")
                append("Ctrl+L to mark as learned, ")
                append("Ctrl+N to mark as not learned")
            }
            accessibilityService.announceForAccessibility(instructions)
        }
    }
}

/**
 * Focus management utilities
 */
@Composable
fun rememberFocusManager(): androidx.compose.ui.focus.FocusManager {
    return LocalFocusManager.current
}

/**
 * Composable that manages focus order for a list of items
 */
@Composable
fun FocusOrderManager(
    itemCount: Int,
    currentIndex: Int,
    onIndexChanged: (Int) -> Unit,
    content: @Composable (index: Int, focusRequester: FocusRequester) -> Unit
) {
    val focusRequesters = remember(itemCount) {
        List(itemCount) { FocusRequester() }
    }
    
    LaunchedEffect(currentIndex) {
        if (currentIndex in 0 until itemCount) {
            focusRequesters[currentIndex].requestFocus()
        }
    }
    
    for (index in 0 until itemCount) {
        content(index, focusRequesters[index])
    }
}

/**
 * Modifier for skip links (accessibility navigation)
 */
@Composable
fun Modifier.skipLink(
    targetDescription: String,
    onActivate: () -> Unit
): Modifier {
    return this.keyboardNavigable(
        onSelect = onActivate
    )
}

/**
 * Composable wrapper that provides comprehensive keyboard navigation
 */
@Composable
fun KeyboardNavigationWrapper(
    modifier: Modifier = Modifier,
    showInstructions: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        content()
        
        if (showInstructions) {
            KeyboardNavigationInstructions()
        }
    }
}