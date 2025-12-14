package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.ui.components.FlashcardCarousel
import com.example.sakura_flashcard.ui.components.InteractionType
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun HomeScreen(
    user: User?,
    recommendedFlashcards: List<Flashcard>,
    customDecks: List<CustomDeck> = emptyList(),
    onFlashcardInteraction: (Flashcard, InteractionType) -> Unit,
    onTopicSelected: (VocabularyTopic) -> Unit,
    onLevelSelected: (VocabularyTopic, JLPTLevel) -> Unit,
    onCustomDeckSelected: (CustomDeck) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTopic by remember { mutableStateOf<VocabularyTopic?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (selectedTopic == null) {
            WelcomeHeader(user = user, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            FlashcardCarousel(
                flashcards = recommendedFlashcards,
                onCardInteraction = onFlashcardInteraction,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        VocabularyTopicsGrid(
            selectedTopic = selectedTopic,
            onTopicSelected = { topic ->
                selectedTopic = topic
                onTopicSelected(topic)
            },
            onLevelSelected = onLevelSelected,
            onBack = { selectedTopic = null },
            modifier = Modifier.fillMaxWidth()
        )

        // Section: Bộ thẻ của bạn
        if (selectedTopic == null && customDecks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            CustomDecksSection(
                customDecks = customDecks,
                onCustomDeckSelected = onCustomDeckSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WelcomeHeader(user: User?, modifier: Modifier = Modifier) {
    val gradientColors = listOf(
        Color(0xFFfdf2f8), // pink-50
        Color(0xFFfce7f3), // pink-100
        Color(0xFFfff1f2)  // rose-50
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = gradientColors),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sakura icon
                Text(
                    text = "\uD83C\uDF38", // Cherry blossom emoji
                    fontSize = 40.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (user != null) "Chào mừng trở lại," else "Chào mừng đến với",
                    style = AppTypography.BodyMedium,
                    color = AppColors.TextSecondary
                )
                
                Text(
                    text = user?.username ?: "Sakura Flashcard",
                    style = AppTypography.HeadlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFbe185d) // pink-700
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Hãy học tiếng Nhật hôm nay! \uD83D\uDCDA",
                    style = AppTypography.BodyMedium,
                    color = AppColors.TextSecondary
                )

                if (user != null) {
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge(
                            icon = Icons.Rounded.Style,
                            value = user.learningProgress.flashcardsLearned.toString(),
                            label = "Thẻ",
                            bgColor = Color(0xFFdbeafe), // blue-100
                            iconColor = Color(0xFF3b82f6) // blue-500
                        )
                        StatBadge(
                            icon = Icons.Rounded.Quiz,
                            value = user.learningProgress.quizzesCompleted.toString(),
                            label = "Bài kiểm tra",
                            bgColor = Color(0xFFd1fae5), // emerald-100
                            iconColor = Color(0xFF10b981) // emerald-500
                        )
                        StatBadge(
                            icon = Icons.Rounded.LocalFireDepartment,
                            value = user.learningProgress.currentStreak.toString(),
                            label = "Chuỗi ngày",
                            bgColor = Color(0xFFfed7aa), // orange-200
                            iconColor = Color(0xFFf97316) // orange-500
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    icon: ImageVector,
    value: String,
    label: String,
    bgColor: Color,
    iconColor: Color
) {
    Column(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.TitleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Text(
            text = label,
            style = AppTypography.LabelSmall,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
private fun VocabularyTopicsGrid(
    selectedTopic: VocabularyTopic?,
    onTopicSelected: (VocabularyTopic) -> Unit,
    onLevelSelected: (VocabularyTopic, JLPTLevel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "\uD83D\uDCDA", // Books emoji
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Chủ đề học tập",
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }

        if (selectedTopic == null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(520.dp)
            ) {
                items(VocabularyTopic.values().toList()) { topic ->
                    TopicCard(topic = topic, onClick = { onTopicSelected(topic) })
                }
            }
        } else {
            JLPTLevelSelection(
                topic = selectedTopic,
                onLevelSelected = { level -> onLevelSelected(selectedTopic, level) },
                onBack = onBack
            )
        }
    }
}

private fun getTopicEmoji(topic: VocabularyTopic): String = when (topic) {
    VocabularyTopic.ANIME -> "\uD83C\uDFAC"        // Movie camera
    VocabularyTopic.BODY_PARTS -> "\uD83E\uDEC0"  // Anatomical heart
    VocabularyTopic.FOOD -> "\uD83C\uDF71"        // Bento box
    VocabularyTopic.DAILY_LIFE -> "\uD83C\uDFE0" // House
    VocabularyTopic.ANIMALS -> "\uD83D\uDC31"    // Cat
    VocabularyTopic.SCHOOL -> "\uD83D\uDCDA"     // Books
    VocabularyTopic.TRAVEL -> "\u2708\uFE0F"     // Airplane
    VocabularyTopic.WEATHER -> "\u26C5"          // Sun behind cloud
    VocabularyTopic.FAMILY -> "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67" // Family
    VocabularyTopic.TECHNOLOGY -> "\uD83D\uDCBB" // Laptop
    VocabularyTopic.CLOTHES -> "\uD83D\uDC55"    // T-shirt
    VocabularyTopic.COLORS -> "\uD83C\uDFA8"     // Artist palette
    VocabularyTopic.NUMBERS -> "\uD83D\uDD22"    // Numbers
    VocabularyTopic.COMMON_EXPRESSIONS -> "\uD83D\uDCAC" // Speech bubble
}

private fun getTopicColor(topic: VocabularyTopic): Color = when (topic) {
    VocabularyTopic.ANIME -> Color(0xFFfef3c7)        // amber-100
    VocabularyTopic.BODY_PARTS -> Color(0xFFfce7f3)   // pink-100
    VocabularyTopic.FOOD -> Color(0xFFffedd5)         // orange-100
    VocabularyTopic.DAILY_LIFE -> Color(0xFFdbeafe)   // blue-100
    VocabularyTopic.ANIMALS -> Color(0xFFd1fae5)      // emerald-100
    VocabularyTopic.SCHOOL -> Color(0xFFede9fe)       // violet-100
    VocabularyTopic.TRAVEL -> Color(0xFFcffafe)       // cyan-100
    VocabularyTopic.WEATHER -> Color(0xFFfef9c3)      // yellow-100
    VocabularyTopic.FAMILY -> Color(0xFFfce7f3)       // pink-100
    VocabularyTopic.TECHNOLOGY -> Color(0xFFe0e7ff)   // indigo-100
    VocabularyTopic.CLOTHES -> Color(0xFFf5d0fe)      // fuchsia-100
    VocabularyTopic.COLORS -> Color(0xFFfbcfe8)       // pink-200
    VocabularyTopic.NUMBERS -> Color(0xFFbfdbfe)      // blue-200
    VocabularyTopic.COMMON_EXPRESSIONS -> Color(0xFFa7f3d0) // emerald-200
}

@Composable
private fun TopicCard(topic: VocabularyTopic, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = getTopicColor(topic)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = getTopicEmoji(topic),
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = topic.displayName,
                    style = AppTypography.BodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CustomDecksSection(
    customDecks: List<CustomDeck>,
    onCustomDeckSelected: (CustomDeck) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "📚",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bộ thẻ của bạn",
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(customDecks) { deck ->
                CustomDeckTopicCard(
                    deck = deck,
                    onClick = { onCustomDeckSelected(deck) }
                )
            }
        }
    }
}

@Composable
private fun CustomDeckTopicCard(deck: CustomDeck, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFf0fdf4)), // green-50
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFF86efac)), // green-300
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "\uD83D\uDCDD", // Memo emoji
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = deck.name,
                    style = AppTypography.BodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = "${deck.flashcardCount} thẻ",
                    style = AppTypography.LabelSmall,
                    color = Color(0xFF16a34a) // green-600
                )
            }
        }
    }
}

@Composable
private fun JLPTLevelSelection(
    topic: VocabularyTopic,
    onLevelSelected: (JLPTLevel) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Back button with topic info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = AppColors.PrimaryLight
                )
            }
            Text(
                text = getTopicEmoji(topic),
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = topic.displayName,
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "\u26A1 Chọn cấp độ JLPT",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            JLPTLevel.values().forEach { level ->
                JLPTLevelCard(level = level, onClick = { onLevelSelected(level) })
            }
        }
    }
}

private fun getLevelColor(level: JLPTLevel): Color = when (level) {
    JLPTLevel.N5 -> Color(0xFF22c55e) // green-500
    JLPTLevel.N4 -> Color(0xFF84cc16) // lime-500
    JLPTLevel.N3 -> Color(0xFFeab308) // yellow-500
    JLPTLevel.N2 -> Color(0xFFf97316) // orange-500
    JLPTLevel.N1 -> Color(0xFFef4444) // red-500
}

private fun getLevelBackgroundColor(level: JLPTLevel): Color = when (level) {
    JLPTLevel.N5 -> Color(0xFFdcfce7) // green-100
    JLPTLevel.N4 -> Color(0xFFecfccb) // lime-100
    JLPTLevel.N3 -> Color(0xFFfef9c3) // yellow-100
    JLPTLevel.N2 -> Color(0xFFffedd5) // orange-100
    JLPTLevel.N1 -> Color(0xFFfee2e2) // red-100
}

private fun getLevelStars(level: JLPTLevel): String = when (level) {
    JLPTLevel.N5 -> "\u2B50"
    JLPTLevel.N4 -> "\u2B50\u2B50"
    JLPTLevel.N3 -> "\u2B50\u2B50\u2B50"
    JLPTLevel.N2 -> "\u2B50\u2B50\u2B50\u2B50"
    JLPTLevel.N1 -> "\u2B50\u2B50\u2B50\u2B50\u2B50"
}

@Composable
private fun JLPTLevelCard(
    level: JLPTLevel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColor = getLevelColor(level)
    val backgroundColor = getLevelBackgroundColor(level)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level badge
            Surface(
                color = levelColor,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = level.displayName,
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Description and stars
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = level.description.substringAfter(" - "),
                    style = AppTypography.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = getLevelStars(level),
                    fontSize = 12.sp
                )
            }
            
            // Play icon
            Icon(
                imageVector = Icons.Rounded.PlayCircle,
                contentDescription = "Bắt đầu ${level.displayName}",
                tint = levelColor,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
