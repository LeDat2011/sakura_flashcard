package com.example.sakura_flashcard.ui.theme

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * Accessibility utilities for Material You theme compliance and enhanced accessibility features
 */
object AccessibilityUtils {
    
    // Minimum touch target size as per Material Design guidelines
    val MinimumTouchTargetSize = 48.dp
    
    // Recommended touch target size for better accessibility
    val RecommendedTouchTargetSize = 56.dp
    
    // Animation duration constants for reduced motion
    const val STANDARD_ANIMATION_DURATION = 300
    const val REDUCED_MOTION_DURATION = 150
    const val NO_MOTION_DURATION = 0
    
    // Font scale constants
    const val MIN_FONT_SCALE = 0.85f
    const val MAX_FONT_SCALE = 2.0f
    const val DEFAULT_FONT_SCALE = 1.0f
    
    /**
     * Calculates the contrast ratio between two colors
     * Returns a value between 1 and 21, where higher values indicate better contrast
     */
    fun calculateContrastRatio(color1: Color, color2: Color): Float {
        val luminance1 = color1.luminance()
        val luminance2 = color2.luminance()
        
        val lighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * Checks if the contrast ratio meets WCAG AA standards (4.5:1 for normal text)
     */
    fun meetsWCAGAAStandard(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= 4.5f
    }
    
    /**
     * Checks if the contrast ratio meets WCAG AAA standards (7:1 for normal text)
     */
    fun meetsWCAGAAAStandard(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= 7.0f
    }
    
    /**
     * Checks if the contrast ratio meets WCAG AA standards for large text (3:1)
     */
    fun meetsWCAGAALargeText(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= 3.0f
    }
    
    /**
     * Returns an accessible color that meets contrast requirements
     */
    fun getAccessibleColor(
        preferredColor: Color,
        backgroundColor: Color,
        requireAAA: Boolean = false
    ): Color {
        val requiredRatio = if (requireAAA) 7.0f else 4.5f
        
        if (calculateContrastRatio(preferredColor, backgroundColor) >= requiredRatio) {
            return preferredColor
        }
        
        // If preferred color doesn't meet requirements, return high contrast alternative
        return if (backgroundColor.luminance() > 0.5f) {
            Color.Black // Dark text on light background
        } else {
            Color.White // Light text on dark background
        }
    }
    
    /**
     * Checks if the device has accessibility services enabled
     */
    fun isAccessibilityEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        return accessibilityManager.isEnabled
    }
    
    /**
     * Checks if TalkBack or other screen readers are enabled
     */
    fun isScreenReaderEnabled(context: Context): Boolean {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        return accessibilityManager.isTouchExplorationEnabled
    }
    
    /**
     * Gets the system font scale setting
     */
    fun getSystemFontScale(context: Context): Float {
        return context.resources.configuration.fontScale
    }
    
    /**
     * Checks if the user has enabled reduced motion/animation scale
     */
    fun isReducedMotionEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val animationScale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            animationScale == 0.0f || animationScale < 0.5f
        } else {
            false
        }
    }
    
    /**
     * Gets appropriate animation duration based on accessibility preferences
     */
    fun getAnimationDuration(context: Context, standardDuration: Int = STANDARD_ANIMATION_DURATION): Int {
        return when {
            isReducedMotionEnabled(context) -> NO_MOTION_DURATION
            isAccessibilityEnabled(context) -> REDUCED_MOTION_DURATION
            else -> standardDuration
        }
    }
    
    /**
     * Validates font scale is within acceptable bounds
     */
    fun validateFontScale(scale: Float): Float {
        return scale.coerceIn(MIN_FONT_SCALE, MAX_FONT_SCALE)
    }
    
    /**
     * Creates accessible content description for Japanese text with pronunciation
     */
    fun createJapaneseContentDescription(
        text: String,
        pronunciation: String? = null,
        translation: String? = null
    ): String {
        val parts = mutableListOf<String>()
        
        parts.add("Japanese text: $text")
        
        if (!pronunciation.isNullOrBlank()) {
            parts.add("Pronunciation: $pronunciation")
        }
        
        if (!translation.isNullOrBlank()) {
            parts.add("Translation: $translation")
        }
        
        return parts.joinToString(", ")
    }
    
    /**
     * Creates content description for flashcard status
     */
    fun createFlashcardStatusDescription(isLearned: Boolean): String {
        return if (isLearned) {
            "Flashcard marked as learned. Tap to mark as not learned."
        } else {
            "Flashcard not learned. Tap to mark as learned."
        }
    }
    
    /**
     * Creates content description for quiz progress
     */
    fun createQuizProgressDescription(current: Int, total: Int): String {
        return "Question $current of $total"
    }
}

/**
 * Modifier that ensures minimum touch target size for accessibility
 */
@Composable
fun Modifier.accessibleTouchTarget(
    minimumSize: Dp = AccessibilityUtils.MinimumTouchTargetSize
): Modifier {
    return this.size(minimumSize)
}

/**
 * Composable that provides accessible color combinations
 */
@Composable
fun accessibleColors(): AccessibleColors {
    val colorScheme = MaterialTheme.colorScheme
    
    return AccessibleColors(
        primaryOnBackground = AccessibilityUtils.getAccessibleColor(
            colorScheme.primary,
            colorScheme.background
        ),
        secondaryOnBackground = AccessibilityUtils.getAccessibleColor(
            colorScheme.secondary,
            colorScheme.background
        ),
        onPrimaryContainer = AccessibilityUtils.getAccessibleColor(
            colorScheme.onPrimaryContainer,
            colorScheme.primaryContainer
        ),
        onSecondaryContainer = AccessibilityUtils.getAccessibleColor(
            colorScheme.onSecondaryContainer,
            colorScheme.secondaryContainer
        )
    )
}

/**
 * Data class holding accessible color combinations
 */
data class AccessibleColors(
    val primaryOnBackground: Color,
    val secondaryOnBackground: Color,
    val onPrimaryContainer: Color,
    val onSecondaryContainer: Color
)

/**
 * Validates that the current theme meets accessibility standards
 */
@Composable
fun validateThemeAccessibility(): ThemeAccessibilityReport {
    val colorScheme = MaterialTheme.colorScheme
    
    val primaryBackgroundContrast = AccessibilityUtils.calculateContrastRatio(
        colorScheme.primary,
        colorScheme.background
    )
    
    val onSurfaceContrast = AccessibilityUtils.calculateContrastRatio(
        colorScheme.onSurface,
        colorScheme.surface
    )
    
    val onPrimaryContrast = AccessibilityUtils.calculateContrastRatio(
        colorScheme.onPrimary,
        colorScheme.primary
    )
    
    return ThemeAccessibilityReport(
        primaryBackgroundContrastRatio = primaryBackgroundContrast,
        onSurfaceContrastRatio = onSurfaceContrast,
        onPrimaryContrastRatio = onPrimaryContrast,
        meetsWCAGAA = AccessibilityUtils.meetsWCAGAAStandard(colorScheme.onSurface, colorScheme.surface),
        meetsWCAGAAA = AccessibilityUtils.meetsWCAGAAAStandard(colorScheme.onSurface, colorScheme.surface)
    )
}

/**
 * Report of theme accessibility compliance
 */
data class ThemeAccessibilityReport(
    val primaryBackgroundContrastRatio: Float,
    val onSurfaceContrastRatio: Float,
    val onPrimaryContrastRatio: Float,
    val meetsWCAGAA: Boolean,
    val meetsWCAGAAA: Boolean
)

/**
 * Custom semantic properties for Japanese learning content
 */
val JapanesePronunciation = SemanticsPropertyKey<String>("JapanesePronunciation")
val JapaneseTranslation = SemanticsPropertyKey<String>("JapaneseTranslation")
val FlashcardSide = SemanticsPropertyKey<String>("FlashcardSide")
val LearningStatus = SemanticsPropertyKey<String>("LearningStatus")

/**
 * Extension functions for semantic properties
 */
fun SemanticsPropertyReceiver.japanesePronunciation(pronunciation: String) {
    this[JapanesePronunciation] = pronunciation
}

fun SemanticsPropertyReceiver.japaneseTranslation(translation: String) {
    this[JapaneseTranslation] = translation
}

fun SemanticsPropertyReceiver.flashcardSide(side: String) {
    this[FlashcardSide] = side
}

fun SemanticsPropertyReceiver.learningStatus(status: String) {
    this[LearningStatus] = status
}

/**
 * Accessibility state for the application
 */
@Composable
fun rememberAccessibilityState(): AccessibilityState {
    val context = LocalContext.current
    
    return AccessibilityState(
        isScreenReaderEnabled = AccessibilityUtils.isScreenReaderEnabled(context),
        isAccessibilityEnabled = AccessibilityUtils.isAccessibilityEnabled(context),
        isReducedMotionEnabled = AccessibilityUtils.isReducedMotionEnabled(context),
        systemFontScale = AccessibilityUtils.getSystemFontScale(context)
    )
}

/**
 * Data class holding current accessibility state
 */
data class AccessibilityState(
    val isScreenReaderEnabled: Boolean,
    val isAccessibilityEnabled: Boolean,
    val isReducedMotionEnabled: Boolean,
    val systemFontScale: Float
)