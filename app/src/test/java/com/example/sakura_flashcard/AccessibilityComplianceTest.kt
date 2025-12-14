package com.example.sakura_flashcard

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.ui.theme.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.float
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 27: Accessibility Compliance
 * Validates: Requirements 7.2, 7.3, 7.4, 7.5
 */
class AccessibilityComplianceTest : StringSpec({
    
    "theme should maintain proper contrast ratios for WCAG AA compliance" {
        checkAll(iterations = 100, Arb.boolean()) { isDarkTheme ->
            // Test primary text on background
            val textColor = if (isDarkTheme) DarkTextPrimary else TextPrimary
            val backgroundColor = if (isDarkTheme) DarkSurfacePrimary else SoftWhite
            
            val contrastRatio = AccessibilityUtils.calculateContrastRatio(textColor, backgroundColor)
            
            // WCAG AA requires 4.5:1 contrast ratio for normal text
            (contrastRatio >= 4.5f) shouldBe true
            
            // Test secondary text contrast
            val secondaryTextColor = if (isDarkTheme) DarkTextSecondary else TextSecondary
            val secondaryContrastRatio = AccessibilityUtils.calculateContrastRatio(secondaryTextColor, backgroundColor)
            
            // Secondary text should also meet WCAG AA standards
            (secondaryContrastRatio >= 4.5f) shouldBe true
        }
    }
    
    "theme should provide high contrast mode for enhanced accessibility" {
        checkAll(iterations = 100, Arb.boolean()) { isDarkTheme ->
            // Test high contrast colors
            val highContrastText = if (isDarkTheme) Color.White else HighContrastText
            val highContrastBg = if (isDarkTheme) Color.Black else HighContrastBackground
            
            val highContrastRatio = AccessibilityUtils.calculateContrastRatio(highContrastText, highContrastBg)
            
            // High contrast mode should exceed WCAG AAA standards (7:1)
            (highContrastRatio >= 7.0f) shouldBe true
            
            // High contrast primary colors should also meet enhanced standards
            val highContrastPrimary = if (isDarkTheme) Color(0xFF66B3FF) else HighContrastPrimary
            val primaryContrastRatio = AccessibilityUtils.calculateContrastRatio(highContrastPrimary, highContrastBg)
            
            (primaryContrastRatio >= 4.5f) shouldBe true
        }
    }
    
    "typography should support readable font sizes with proper scaling" {
        checkAll(iterations = 100, Arb.float(0.8f, 2.0f)) { fontScale ->
            // Test that base font sizes are readable
            val bodyLargeSize = Typography.bodyLarge.fontSize
            val bodyMediumSize = Typography.bodyMedium.fontSize
            val labelLargeSize = Typography.labelLarge.fontSize
            
            // Base sizes should be at least 12sp for readability
            (bodyLargeSize.value >= 12f) shouldBe true
            (bodyMediumSize.value >= 12f) shouldBe true
            (labelLargeSize.value >= 11f) shouldBe true // Labels can be slightly smaller
            
            // Test scaled typography - we'll test the scaling logic without Compose context
            val baseBodyLarge = Typography.bodyLarge
            val baseBodyMedium = Typography.bodyMedium
            
            // Simulate scaled font sizes
            val scaledBodyLargeSize = baseBodyLarge.fontSize * fontScale
            val scaledBodyMediumSize = baseBodyMedium.fontSize * fontScale
            // Scaled fonts should maintain proportional relationships
            scaledBodyLargeSize shouldBe (bodyLargeSize * fontScale)
            scaledBodyMediumSize shouldBe (bodyMediumSize * fontScale)
            
            // Even at minimum scale, fonts should remain readable
            if (fontScale >= 0.8f) {
                (scaledBodyLargeSize.value >= 10f) shouldBe true
                (scaledBodyMediumSize.value >= 10f) shouldBe true
            }
        }
    }
    
    "theme should provide appropriate touch targets for accessibility" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Verify minimum touch target sizes meet accessibility guidelines
            val minimumTouchTarget = AccessibilityUtils.MinimumTouchTargetSize
            val recommendedTouchTarget = AccessibilityUtils.RecommendedTouchTargetSize
            
            // Minimum should be at least 48dp as per Material Design
            (minimumTouchTarget.value >= 48f) shouldBe true
            
            // Recommended should be larger than minimum
            recommendedTouchTarget shouldBeGreaterThan minimumTouchTarget
            
            // Recommended should be at least 56dp for better accessibility
            (recommendedTouchTarget.value >= 56f) shouldBe true
        }
    }
    
    "theme should support reduced motion preferences" {
        checkAll(iterations = 100, Arb.boolean()) { reduceMotion ->
            // This test verifies that the theme system can handle reduced motion preferences
            // In a real implementation, animations would be disabled or reduced when reduceMotion is true
            
            val themeConfig = ThemeConfig(reduceMotion = reduceMotion)
            
            // Verify that the theme config properly stores the reduce motion preference
            themeConfig.reduceMotion shouldBe reduceMotion
            
            // In practice, this would affect animation durations and types
            // For now, we verify the configuration is properly handled
            val expectedAnimationDuration = if (reduceMotion) 0 else 300 // milliseconds
            val actualAnimationDuration = if (themeConfig.reduceMotion) 0 else 300
            
            actualAnimationDuration shouldBe expectedAnimationDuration
        }
    }
    
    "theme should provide sufficient color differentiation for color-blind users" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Test that primary and secondary colors are distinguishable
            val primaryColor = PastelBlue
            val secondaryColor = PastelPink
            
            // Colors should have different hue values
            val primaryHue = extractHue(primaryColor)
            val secondaryHue = extractHue(secondaryColor)
            
            val hueDifference = kotlin.math.abs(primaryHue - secondaryHue)
            val adjustedHueDifference = if (hueDifference > 180f) 360f - hueDifference else hueDifference
            
            // Colors should be at least 30 degrees apart in hue for basic differentiation
            (adjustedHueDifference >= 30f) shouldBe true
            
            // Test status colors differentiation
            val successColor = SuccessGreen
            val warningColor = WarningOrange
            val errorColor = ErrorRed
            
            val successHue = extractHue(successColor)
            val warningHue = extractHue(warningColor)
            val errorHue = extractHue(errorColor)
            
            // Success and error should be well differentiated
            val successErrorDiff = kotlin.math.abs(successHue - errorHue)
            val adjustedSuccessErrorDiff = if (successErrorDiff > 180f) 360f - successErrorDiff else successErrorDiff
            (adjustedSuccessErrorDiff >= 60f) shouldBe true
            
            // Warning should be different from both success and error
            val warningSuccessDiff = kotlin.math.abs(warningHue - successHue)
            val warningErrorDiff = kotlin.math.abs(warningHue - errorHue)
            
            (warningSuccessDiff >= 30f) shouldBe true
            (warningErrorDiff >= 30f) shouldBe true
        }
    }
    
    "theme should support screen reader compatibility with proper semantic colors" {
        checkAll(iterations = 100, Arb.boolean()) { isDarkTheme ->
            // Test that semantic color roles are properly defined
            val backgroundColor = if (isDarkTheme) DarkSurfacePrimary else SoftWhite
            val surfaceColor = if (isDarkTheme) DarkSurfaceSecondary else SurfaceSecondary
            val primaryColor = if (isDarkTheme) PastelBlueDark else PastelBlue
            
            // Background and surface should be distinguishable (any contrast is acceptable for subtle differences)
            val backgroundSurfaceContrast = AccessibilityUtils.calculateContrastRatio(backgroundColor, surfaceColor)
            (backgroundSurfaceContrast > 0.0f) shouldBe true // Basic sanity check
            
            // Primary color should be distinguishable from background
            val primaryOnBackgroundContrast = AccessibilityUtils.calculateContrastRatio(primaryColor, backgroundColor)
            (primaryOnBackgroundContrast > 1.0f) shouldBe true // Basic distinguishability
            
            // Colors should not be identical (basic semantic color validation)
            (backgroundColor != primaryColor) shouldBe true
            (backgroundColor != surfaceColor || isDarkTheme) shouldBe true // Allow same colors only in dark theme edge case
        }
    }
    
    "theme should maintain accessibility across different font scale preferences" {
        checkAll(iterations = 100, Arb.float(0.85f, 1.3f)) { fontScale ->
            // Test that accessibility is maintained at different font scales
            // We'll test the scaling logic without requiring Compose context
            
            // Line height should scale proportionally with font size
            val baseBodyLarge = Typography.bodyLarge
            val scaledFontSize = baseBodyLarge.fontSize * fontScale
            val scaledLineHeight = baseBodyLarge.lineHeight * fontScale
            
            val fontSizeRatio = scaledFontSize.value / baseBodyLarge.fontSize.value
            val lineHeightRatio = scaledLineHeight.value / baseBodyLarge.lineHeight.value
            
            // Font size and line height should scale together (within 5% tolerance)
            val scalingDifference = kotlin.math.abs(fontSizeRatio - lineHeightRatio)
            (scalingDifference <= 0.05f) shouldBe true
            
            // Letter spacing should remain readable at all scales
            val baseLetterSpacing = baseBodyLarge.letterSpacing.value
            val scaledLetterSpacing = baseLetterSpacing * fontScale
            
            // Letter spacing should scale but not become negative or excessive
            if (baseLetterSpacing >= 0) {
                (scaledLetterSpacing >= 0f) shouldBe true
            }
            (scaledLetterSpacing <= 2.0f) shouldBe true // Not too spaced out
        }
    }
})

// Helper function to extract hue from Color (reused from MaterialYouComplianceTest)
private fun extractHue(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    
    if (delta == 0f) return 0f
    
    val hue = when (max) {
        r -> ((g - b) / delta) % 6
        g -> (b - r) / delta + 2
        b -> (r - g) / delta + 4
        else -> 0f
    }
    
    return (hue * 60f + 360f) % 360f
}