package com.example.sakura_flashcard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Base Material typography styles with accessibility considerations
private val BaseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )
)

// Default typography for standard use
val Typography = BaseTypography

// Function to create scaled typography based on accessibility preferences
@Composable
fun createScaledTypography(fontScale: Float): Typography {
    return Typography(
        displayLarge = BaseTypography.displayLarge.copy(
            fontSize = BaseTypography.displayLarge.fontSize * fontScale,
            lineHeight = BaseTypography.displayLarge.lineHeight * fontScale
        ),
        displayMedium = BaseTypography.displayMedium.copy(
            fontSize = BaseTypography.displayMedium.fontSize * fontScale,
            lineHeight = BaseTypography.displayMedium.lineHeight * fontScale
        ),
        displaySmall = BaseTypography.displaySmall.copy(
            fontSize = BaseTypography.displaySmall.fontSize * fontScale,
            lineHeight = BaseTypography.displaySmall.lineHeight * fontScale
        ),
        headlineLarge = BaseTypography.headlineLarge.copy(
            fontSize = BaseTypography.headlineLarge.fontSize * fontScale,
            lineHeight = BaseTypography.headlineLarge.lineHeight * fontScale
        ),
        headlineMedium = BaseTypography.headlineMedium.copy(
            fontSize = BaseTypography.headlineMedium.fontSize * fontScale,
            lineHeight = BaseTypography.headlineMedium.lineHeight * fontScale
        ),
        headlineSmall = BaseTypography.headlineSmall.copy(
            fontSize = BaseTypography.headlineSmall.fontSize * fontScale,
            lineHeight = BaseTypography.headlineSmall.lineHeight * fontScale
        ),
        titleLarge = BaseTypography.titleLarge.copy(
            fontSize = BaseTypography.titleLarge.fontSize * fontScale,
            lineHeight = BaseTypography.titleLarge.lineHeight * fontScale
        ),
        titleMedium = BaseTypography.titleMedium.copy(
            fontSize = BaseTypography.titleMedium.fontSize * fontScale,
            lineHeight = BaseTypography.titleMedium.lineHeight * fontScale
        ),
        titleSmall = BaseTypography.titleSmall.copy(
            fontSize = BaseTypography.titleSmall.fontSize * fontScale,
            lineHeight = BaseTypography.titleSmall.lineHeight * fontScale
        ),
        bodyLarge = BaseTypography.bodyLarge.copy(
            fontSize = BaseTypography.bodyLarge.fontSize * fontScale,
            lineHeight = BaseTypography.bodyLarge.lineHeight * fontScale
        ),
        bodyMedium = BaseTypography.bodyMedium.copy(
            fontSize = BaseTypography.bodyMedium.fontSize * fontScale,
            lineHeight = BaseTypography.bodyMedium.lineHeight * fontScale
        ),
        bodySmall = BaseTypography.bodySmall.copy(
            fontSize = BaseTypography.bodySmall.fontSize * fontScale,
            lineHeight = BaseTypography.bodySmall.lineHeight * fontScale
        ),
        labelLarge = BaseTypography.labelLarge.copy(
            fontSize = BaseTypography.labelLarge.fontSize * fontScale,
            lineHeight = BaseTypography.labelLarge.lineHeight * fontScale
        ),
        labelMedium = BaseTypography.labelMedium.copy(
            fontSize = BaseTypography.labelMedium.fontSize * fontScale,
            lineHeight = BaseTypography.labelMedium.lineHeight * fontScale
        ),
        labelSmall = BaseTypography.labelSmall.copy(
            fontSize = BaseTypography.labelSmall.fontSize * fontScale,
            lineHeight = BaseTypography.labelSmall.lineHeight * fontScale
        )
    )
}