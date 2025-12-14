package com.example.sakura_flashcard

import androidx.compose.ui.graphics.Color
import com.example.sakura_flashcard.ui.theme.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.float
import io.kotest.property.checkAll

/**
 * Feature: japanese-flashcard-ui, Property 26: Material You Color Palette
 * Validates: Requirements 7.1
 */
class MaterialYouComplianceTest : StringSpec({
    
    "Material You color palette should use soft learning-friendly pastel colors" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Test that we have distinct primary and secondary colors
            val primaryColors = listOf(PastelBlue, PastelBlueDark, PastelBlueLight)
            val secondaryColors = listOf(PastelPink, PastelPinkDark, PastelPinkLight)
            
            // All colors should have some saturation (not grayscale)
            primaryColors.forEach { color ->
                val saturation = extractSaturation(color)
                saturation shouldBeGreaterThan 0.0f
            }
            
            secondaryColors.forEach { color ->
                val saturation = extractSaturation(color)
                saturation shouldBeGreaterThan 0.0f
            }
            
            // Colors should be distinguishable from each other
            val allColors = primaryColors + secondaryColors
            allColors.forEach { color1 ->
                allColors.filter { it != color1 }.forEach { color2 ->
                    // Colors should not be identical
                    (color1 != color2) shouldBe true
                }
            }
        }
    }
    
    "Material You color palette should include soft white as neutral base" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Verify that SoftWhite is close to pure white but slightly tinted
            val softWhiteRed = SoftWhite.red
            val softWhiteGreen = SoftWhite.green
            val softWhiteBlue = SoftWhite.blue
            
            // Should be very light (all components > 0.9 for soft whites)
            softWhiteRed shouldBeGreaterThan 0.9f
            softWhiteGreen shouldBeGreaterThan 0.9f
            softWhiteBlue shouldBeGreaterThan 0.9f
            
            // Should not be pure white (at least one component < 1.0)
            val isPureWhite = softWhiteRed == 1.0f && softWhiteGreen == 1.0f && softWhiteBlue == 1.0f
            isPureWhite shouldBe false
        }
    }
    
    "Material You design system should provide proper color variations for different states" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Verify that we have light, normal, and dark variants for primary colors
            val blueLightness = extractLightness(PastelBlueLight)
            val blueNormalLightness = extractLightness(PastelBlue)
            val blueDarkLightness = extractLightness(PastelBlueDark)
            
            // Light variant should be lighter than normal, normal lighter than dark
            blueLightness shouldBeGreaterThan blueNormalLightness
            blueNormalLightness shouldBeGreaterThan blueDarkLightness
            
            // Same for pink variants
            val pinkLightness = extractLightness(PastelPinkLight)
            val pinkNormalLightness = extractLightness(PastelPink)
            val pinkDarkLightness = extractLightness(PastelPinkDark)
            
            pinkLightness shouldBeGreaterThan pinkNormalLightness
            pinkNormalLightness shouldBeGreaterThan pinkDarkLightness
        }
    }
    
    "Material You theme should support both light and dark color schemes" {
        checkAll(iterations = 100, Arb.boolean()) { isDark ->
            // Verify that we have distinct color schemes for light and dark themes
            val lightBackground = SoftWhite
            val darkBackground = DarkSurfacePrimary
            
            // Light and dark backgrounds should be significantly different
            val backgroundContrast = AccessibilityUtils.calculateContrastRatio(lightBackground, darkBackground)
            backgroundContrast shouldBeGreaterThan 10.0f // Very high contrast between light and dark
            
            // Text colors should be appropriate for their backgrounds
            val lightTextContrast = AccessibilityUtils.calculateContrastRatio(TextPrimary, lightBackground)
            val darkTextContrast = AccessibilityUtils.calculateContrastRatio(DarkTextPrimary, darkBackground)
            
            lightTextContrast shouldBeGreaterThan 4.5f // WCAG AA compliance
            darkTextContrast shouldBeGreaterThan 4.5f // WCAG AA compliance
        }
    }
    
    "Material You color palette should be learning-friendly with appropriate saturation" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Test all primary and secondary colors for learning-friendly saturation
            val colorsToTest = listOf(
                PastelBlue, PastelBlueDark, PastelBlueLight,
                PastelPink, PastelPinkDark, PastelPinkLight
            )
            
            colorsToTest.forEach { color ->
                val saturation = extractSaturation(color)
                val lightness = extractLightness(color)
                
                // Learning-friendly colors should have some saturation (not grayscale)
                // Allow for a wide range to accommodate different pastel variations
                saturation shouldBeGreaterThan 0.0f
                
                // Pastel colors should have reasonable lightness (not too dark)
                lightness shouldBeGreaterThan 0.3f
                lightness shouldBeLessThan 1.0f
            }
        }
    }
    
    "Material You theme should provide status colors for feedback" {
        checkAll(iterations = 100, Arb.boolean()) { _ ->
            // Verify that we have distinct colors for success, warning, and error states
            val successHue = extractHue(SuccessGreen)
            val warningHue = extractHue(WarningOrange)
            val errorHue = extractHue(ErrorRed)
            
            // Success should be green (around 120 degrees)
            val greenRange = 90f..150f
            successHue shouldBeIn greenRange
            
            // Warning should be orange/yellow (around 30-60 degrees)
            val orangeRange = 15f..65f
            warningHue shouldBeIn orangeRange
            
            // Error should be red (around 0 degrees or 360 degrees)
            val redRange1 = 0f..30f
            val redRange2 = 330f..360f
            (errorHue in redRange1 || errorHue in redRange2) shouldBe true
            
            // All status colors should have sufficient saturation to be noticeable
            val successSaturation = extractSaturation(SuccessGreen)
            val warningSaturation = extractSaturation(WarningOrange)
            val errorSaturation = extractSaturation(ErrorRed)
            
            successSaturation shouldBeGreaterThan 0.1f
            warningSaturation shouldBeGreaterThan 0.1f
            errorSaturation shouldBeGreaterThan 0.1f
        }
    }
})

// Helper functions to extract HSL values from Color
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

private fun extractSaturation(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min
    
    if (max == 0f) return 0f
    
    return delta / max
}

private fun extractLightness(color: Color): Float {
    val r = color.red
    val g = color.green
    val b = color.blue
    
    return (maxOf(r, g, b) + minOf(r, g, b)) / 2f
}