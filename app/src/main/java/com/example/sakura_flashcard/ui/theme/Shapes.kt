package com.example.sakura_flashcard.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SakuraShapes = Shapes(
    // Extra small - for chips, badges
    extraSmall = RoundedCornerShape(8.dp),
    
    // Small - for buttons, small cards
    small = RoundedCornerShape(12.dp),
    
    // Medium - for cards, dialogs
    medium = RoundedCornerShape(16.dp),
    
    // Large - for bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    
    // Extra large - for full screen dialogs
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom shapes for specific components
object CustomShapes {
    val Flashcard = RoundedCornerShape(20.dp)
    val FlashcardLarge = RoundedCornerShape(28.dp)
    val Button = RoundedCornerShape(12.dp)
    val ButtonPill = RoundedCornerShape(50)
    val TopBar = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    val BottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val CharacterCard = RoundedCornerShape(16.dp)
    val GameCard = RoundedCornerShape(20.dp)
    val ProgressBar = RoundedCornerShape(8.dp)
    val Avatar = RoundedCornerShape(50)
    val Badge = RoundedCornerShape(6.dp)
    val Chip = RoundedCornerShape(20.dp)
}
