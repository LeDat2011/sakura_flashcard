package com.example.sakura_flashcard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Elegant Light Color Scheme - Soft, Bright, Minimalist
private val LightColorScheme = lightColorScheme(
    // Primary - Soft Sakura Pink
    primary = PastelBlueDark,             // Accent pink for buttons/highlights
    onPrimary = TextOnPrimary,
    primaryContainer = PastelBlueLight,   // Very light pink containers
    onPrimaryContainer = TextPrimary,
    
    // Secondary - Soft Sage Green
    secondary = PastelPinkDark,           // Green accent
    onSecondary = TextOnPrimary,
    secondaryContainer = PastelPinkLight, // Very light mint
    onSecondaryContainer = TextPrimary,
    
    // Tertiary - Soft Lavender
    tertiary = SakuraGoldDark,
    onTertiary = TextOnPrimary,
    tertiaryContainer = SakuraGoldLight,
    onTertiaryContainer = TextPrimary,
    
    // Status
    error = ErrorRed,
    onError = TextOnPrimary,
    
    // Backgrounds - Clean & Bright
    background = SoftWhite,
    onBackground = TextPrimary,
    
    // Surfaces - Light & Airy
    surface = CardDefault,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondary,
    
    // Outline
    outline = SoftGrayDark,
    outlineVariant = Color(0xFFEEEEEE)
)

// Elegant Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PastelBlue,                 // Soft pink in dark mode
    onPrimary = DarkSurfacePrimary,
    primaryContainer = Color(0xFF37474F), // Dark container
    onPrimaryContainer = PastelBlue,
    
    secondary = PastelPink,
    onSecondary = DarkSurfacePrimary,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = PastelPinkLight,
    
    tertiary = SakuraGold,
    onTertiary = DarkSurfacePrimary,
    
    error = ErrorRed,
    onError = TextOnPrimary,
    
    background = DarkSurfacePrimary,
    onBackground = DarkTextPrimary,
    
    surface = DarkSurfaceSecondary,
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = DarkTextSecondary,
    
    outline = Color(0xFF5A5A5A)
)

// High contrast light color scheme for accessibility
private val HighContrastLightColorScheme = lightColorScheme(
    primary = HighContrastPrimary,
    onPrimary = HighContrastBackground,
    primaryContainer = HighContrastPrimary,
    onPrimaryContainer = HighContrastBackground,
    secondary = HighContrastSecondary,
    onSecondary = HighContrastBackground,
    secondaryContainer = HighContrastSecondary,
    onSecondaryContainer = HighContrastBackground,
    tertiary = HighContrastPrimary,
    onTertiary = HighContrastBackground,
    error = Color(0xFFCC0000),
    onError = HighContrastBackground,
    background = HighContrastBackground,
    onBackground = HighContrastText,
    surface = HighContrastSurface,
    onSurface = HighContrastText,
    surfaceVariant = HighContrastSurface,
    onSurfaceVariant = HighContrastText,
    outline = HighContrastText
)

// High contrast dark color scheme for accessibility
private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFF66B3FF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF66B3FF),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFFFF66B3),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFFF66B3),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFF66B3FF),
    onTertiary = Color(0xFF000000),
    error = Color(0xFFFF6666),
    onError = Color(0xFF000000),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFFFFFFF),
    outline = Color(0xFFFFFFFF)
)

@Composable
fun SakuraFlashcardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    fontScale: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColorScheme
        highContrast && !darkTheme -> HighContrastLightColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val typography = if (fontScale != 1.0f) {
        createScaledTypography(fontScale)
    } else {
        Typography
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use light background color for status bar (elegant look)
            window.statusBarColor = colorScheme.background.toArgb()
            // Light status bar icons for light theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = SakuraShapes,
        content = content
    )
}

// Convenience composable that uses theme preferences
@Composable
fun SakuraFlashcardThemeWithPreferences(
    content: @Composable () -> Unit
) {
    val themeConfig = rememberThemeConfig()
    
    SakuraFlashcardTheme(
        darkTheme = themeConfig.isDarkTheme,
        dynamicColor = themeConfig.useDynamicColor,
        highContrast = themeConfig.useHighContrast,
        fontScale = themeConfig.fontScale,
        content = content
    )
}