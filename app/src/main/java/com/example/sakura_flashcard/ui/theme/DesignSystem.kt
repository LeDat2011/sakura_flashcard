package com.example.sakura_flashcard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ========== COLOR PALETTE ==========
// Inspired from Web Design: Pastel colors with Sky Blue as primary

object AppColors {
    // Primary Colors - Sky Blue Palette
    val PrimaryLight = Color(0xFF0ea5e9)      // sky-500
    val PrimaryLighter = Color(0xFF7dd3fc)    // sky-300
    val PrimaryLightest = Color(0xFFe0f2fe)   // sky-100
    
    // Secondary Colors - Rose/Pink
    val SecondaryLight = Color(0xFFfb7185)    // rose-400
    val SecondaryLighter = Color(0xFFfecdd3)  // rose-200
    val SecondaryLightest = Color(0xFFffe4e6) // rose-100
    
    // Accent Colors - Indigo
    val AccentLight = Color(0xFF6366f1)       // indigo-500
    val AccentLighter = Color(0xFFc7d2fe)     // indigo-300
    val AccentLightest = Color(0xFFe0e7ff)    // indigo-100
    
    // Success - Emerald
    val SuccessLight = Color(0xFF10b981)      // emerald-500
    val SuccessLighter = Color(0xFFd1fae5)    // emerald-200
    val SuccessLightest = Color(0xFFf0fdf4)   // emerald-50
    
    // Warning - Amber/Orange
    val WarningLight = Color(0xFFf97316)      // orange-500
    val WarningLighter = Color(0xFFfed7aa)    // orange-200
    val WarningLightest = Color(0xFFfef3c7)   // amber-100
    
    // Danger - Red
    val DangerLight = Color(0xFFef4444)       // red-500
    val DangerLighter = Color(0xFFfecaca)     // red-200
    val DangerLightest = Color(0xFFfee2e2)    // red-100
    
    // Neutral - Slate/Gray
    val TextPrimary = Color(0xFF1e293b)       // slate-800
    val TextSecondary = Color(0xFF64748b)     // slate-500
    val TextTertiary = Color(0xFF94a3b8)      // slate-400
    val TextDisabled = Color(0xFFcbd5e1)      // slate-300
    
    val SurfaceLight = Color(0xFFFFFFFF)      // white
    val SurfaceMedium = Color(0xFFFFF8FA)     // Very light pink
    val SurfaceDark = Color(0xFFFFF0F5)       // Lavender blush
    val SurfaceBorder = Color(0xFFFFE4E9)     // Light pink border
    
    // Background - Soft Pastel Pink
    val BackgroundPrimary = Color(0xFFFFF0F5)  // Lavender blush
    val BackgroundSecondary = Color(0xFFFFF5F8) // Very light pink
    
    // Category Colors (for topics/badges)
    val CategoryOrange = Color(0xFFfb923c)    // orange-400
    val CategoryGreen = Color(0xFF4ade80)     // green-400
    val CategoryPurple = Color(0xFFc084fc)    // purple-400
    val CategoryBlue = Color(0xFF38bdf8)      // sky-400
    val CategoryPink = Color(0xFFf472b6)      // pink-400
    val CategoryYellow = Color(0xFFfbbf24)    // amber-400
    
    // Special Colors
    val FireRed = Color(0xFFef4444)           // red-500 (for streaks)
    val StarYellow = Color(0xFFfbbf24)        // amber-400 (for stars)
}

// ========== BUTTON STYLES & COMPONENTS ==========
/*
Web Design Patterns:

1. PRIMARY BUTTON (Sky Blue)
   - Background: sky-500 (0x0ea5e9)
   - Text: white
   - Rounded: 16dp / rounded-2xl
   - Padding: 16dp vertical, 20dp horizontal
   - Font Weight: Bold
   
   State:
   - Hover: sky-600
   - Active: scale 0.95
   - Disabled: opacity 50%, cursor not-allowed

2. SECONDARY BUTTON (Light Background)
   - Background: white / slate-50
   - Border: 1dp slate-100
   - Text: slate-700
   - Rounded: 16dp
   - Shadow: minimal (shadow-sm)
   
3. TERTIARY BUTTON (Ghost/Text Only)
   - Background: transparent
   - Text: sky-600
   - Font Weight: Medium
   - Hover: text-sky-700, underline
   
4. ICON BUTTONS
   - Background: sky-100 (when active)
   - Rounded: 12dp / rounded-xl
   - Padding: 8dp
   - Color: sky-600 (active), slate-400 (inactive)

5. BADGE/TAG
   - Background: Light tints (sky-100, rose-100, indigo-100)
   - Text: Dark saturated colors (sky-700, rose-700, indigo-700)
   - Padding: 4dp vertical, 8dp horizontal
   - Rounded: 8dp / rounded-lg
   - Font: 12sp, semibold

6. CARD
   - Background: white
   - Border: 1dp slate-100
   - Rounded: 20-24dp / rounded-3xl
   - Padding: 16-24dp
   - Shadow: shadow-sm
   - Hover: bg-slate-50, scale 1.02
   - Active: scale 0.95

7. BOTTOM NAV ITEM
   - Icon: 24dp
   - Background (active): sky-100
   - Text Color (active): sky-600
   - Text Color (inactive): slate-400
   - Rounded: 12dp / rounded-2xl
   - Animation: -translate-y-1 (shift up on active)

8. TAB BUTTON
   - Background (active): sky-100, border: sky-200
   - Background (inactive): white, border: slate-100
   - Text (active): sky-600
   - Text (inactive): slate-600
   - Rounded: 20dp / rounded-full
   - Padding: 8dp vertical, 16dp horizontal
*/

// ========== SPACING SYSTEM ==========
/*
   4dp = spacing-1
   8dp = spacing-2
  12dp = spacing-3
  16dp = spacing-4
  20dp = spacing-5
  24dp = spacing-6
  32dp = spacing-8
  48dp = spacing-12
  64dp = spacing-16
*/

// ========== BORDER RADIUS ==========
/*
   8dp  = rounded-lg
  12dp  = rounded-xl
  16dp  = rounded-2xl
  20dp  = rounded-3xl (for main cards)
  24dp  = rounded-full (for perfect circles)
*/

// ========== SHADOW SYSTEM ==========
/*
   shadow-sm  = elevation 1dp
   shadow-md  = elevation 4dp
   shadow-lg  = elevation 8dp
   shadow-xl  = elevation 16dp
   
   Default: 0 1px 3px rgba(0,0,0,0.1)
   md:      0 4px 6px -1px rgba(0,0,0,0.1)
   lg:      0 10px 15px -3px rgba(0,0,0,0.1)
*/

// ========== TYPOGRAPHY ==========
object AppTypography {
    // Headlines
    val HeadlineLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
    
    val HeadlineMedium = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
    
    val HeadlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    )
    
    // Titles
    val TitleLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    )
    
    val TitleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    )
    
    // Body
    val BodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    )
    
    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp
    )
    
    val BodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp
    )
    
    // Labels
    val LabelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.1.sp
    )
    
    val LabelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    )
}

// ========== GRADIENT BACKGROUNDS ==========
/*
Web Implementation:
   bg-gradient-to-br from-sky-50 to-blue-50
   bg-gradient-to-r from-orange-50 to-amber-50
   bg-gradient-to-r from-rose-50 to-pink-50
   
Kotlin equivalent:
   Brush.linearGradient(
       colors = listOf(
           Color(0xFFf0f9ff),  // sky-50
           Color(0xFFeff6ff)   // blue-50
       ),
       start = Offset(0f, 0f),
       end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
   )
*/

// Flashcard soft colors - Pastel tones
object FlashcardColors {
    // Soft gradient backgrounds
    val CardGradientStart = Color(0xFFF8FAFC)     // slate-50
    val CardGradientEnd = Color(0xFFF1F5F9)       // slate-100
    
    // Pastel accents for front/back
    val FrontAccent = Color(0xFFe0f2fe)           // sky-100
    val BackAccent = Color(0xFFfce7f3)            // pink-100
    
    // Soft shadows
    val ShadowColor = Color(0xFF94a3b8)           // slate-400
    
    // Learned state
    val LearnedGradientStart = Color(0xFFd1fae5)  // emerald-200
    val LearnedGradientEnd = Color(0xFFa7f3d0)    // emerald-300
    
    // Soft button colors
    val ButtonPrimary = Color(0xFF7dd3fc)         // sky-300
    val ButtonSecondary = Color(0xFFfda4af)       // rose-300
    val ButtonSuccess = Color(0xFF6ee7b7)         // emerald-300
}


