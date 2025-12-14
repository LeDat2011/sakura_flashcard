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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.api.QuizTopicInfo
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun QuizScreen(
    onTopicSelected: (String, String) -> Unit,
    onStartQuiz: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTopic by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.SurfaceMedium)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        QuizHeader(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTopic == null) {
            QuizTopicSelection(
                topics = uiState.topics,
                isLoading = uiState.isLoadingTopics,
                error = uiState.error,
                onTopicSelected = { topicInfo ->
                    selectedTopic = topicInfo.topic
                    onTopicSelected(topicInfo.topic, topicInfo.level)
                },
                onRetry = { viewModel.loadQuizTopics() },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            QuizLevelSelection(
                topicName = selectedTopic!!,
                levels = uiState.topics.filter { it.topic == selectedTopic },
                onStartQuiz = { level -> onStartQuiz(selectedTopic!!, level) },
                onBack = { selectedTopic = null },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuizHeader(modifier: Modifier = Modifier) {
    val gradientColors = listOf(Color(0xFFf5e5ff), Color(0xFFede9fe), Color(0xFFfaf5ff))

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = gradientColors), shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "\uD83D\uDCDD", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Bài kiểm tra tiếng Nhật",
                    style = AppTypography.HeadlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7c3aed),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kiểm tra kiến thức với 10 câu hỏi",
                    style = AppTypography.BodyMedium,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QuizTopicSelection(
    topics: List<QuizTopicInfo>,
    isLoading: Boolean,
    error: String?,
    onTopicSelected: (QuizTopicInfo) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(text = "\uD83C\uDFAF", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Chọn chủ đề", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = AppColors.PrimaryLight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đang tải chủ đề...",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không thể tải chủ đề",
                            style = AppTypography.TitleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thử lại")
                        }
                    }
                }
            }
            topics.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chưa có chủ đề nào",
                            style = AppTypography.TitleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vui lòng quay lại sau",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
            else -> {
                val uniqueTopics = topics.distinctBy { it.topic }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(520.dp)
                ) {
                    items(uniqueTopics) { topicInfo ->
                        QuizTopicCard(
                            topicInfo = topicInfo,
                            onClick = { onTopicSelected(topicInfo) }
                        )
                    }
                }
            }
        }
    }
}

private fun getQuizTopicEmoji(topic: String): String = when (topic.uppercase()) {
    "ANIME" -> "\uD83C\uDFAC"
    "BODY_PARTS", "BODY PARTS" -> "\uD83E\uDEC0"
    "FOOD" -> "\uD83C\uDF71"
    "DAILY_LIFE", "DAILY LIFE" -> "\uD83C\uDFE0"
    "ANIMALS" -> "\uD83D\uDC31"
    "SCHOOL" -> "\uD83D\uDCDA"
    "TRAVEL" -> "\u2708\uFE0F"
    "WEATHER" -> "\u26C5"
    "FAMILY" -> "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67"
    "TECHNOLOGY" -> "\uD83D\uDCBB"
    "CLOTHES" -> "\uD83D\uDC55"
    "COLORS" -> "\uD83C\uDFA8"
    "NUMBERS" -> "\uD83D\uDD22"
    "COMMON_EXPRESSIONS", "COMMON EXPRESSIONS" -> "\uD83D\uDCAC"
    "GREETINGS" -> "👋"
    "TIME" -> "⏰"
    "NATURE" -> "🌿"
    "SPORTS" -> "⚽"
    "MUSIC" -> "🎵"
    "WORK" -> "💼"
    "HEALTH" -> "💊"
    "SHOPPING" -> "🛒"
    else -> "📚"
}

private fun getQuizTopicColor(topic: String): Color = when (topic.uppercase()) {
    "ANIME" -> Color(0xFFfef3c7)
    "BODY_PARTS", "BODY PARTS" -> Color(0xFFfce7f3)
    "FOOD" -> Color(0xFFffedd5)
    "DAILY_LIFE", "DAILY LIFE" -> Color(0xFFdbeafe)
    "ANIMALS" -> Color(0xFFd1fae5)
    "SCHOOL" -> Color(0xFFede9fe)
    "TRAVEL" -> Color(0xFFcffafe)
    "WEATHER" -> Color(0xFFfef9c3)
    "FAMILY" -> Color(0xFFfce7f3)
    "TECHNOLOGY" -> Color(0xFFe0e7ff)
    "CLOTHES" -> Color(0xFFf5d0fe)
    "COLORS" -> Color(0xFFfbcfe8)
    "NUMBERS" -> Color(0xFFbfdbfe)
    "COMMON_EXPRESSIONS", "COMMON EXPRESSIONS" -> Color(0xFFa7f3d0)
    "GREETINGS" -> Color(0xFFfef3c7)
    "TIME" -> Color(0xFFe0e7ff)
    "NATURE" -> Color(0xFFd1fae5)
    "SPORTS" -> Color(0xFFfed7aa)
    "MUSIC" -> Color(0xFFfae8ff)
    "WORK" -> Color(0xFFdbeafe)
    "HEALTH" -> Color(0xFFfecaca)
    "SHOPPING" -> Color(0xFFfce7f3)
    else -> Color(0xFFf3f4f6)
}

private fun getTopicDisplayName(topic: String): String = when (topic.uppercase()) {
    "ANIME" -> "Anime"
    "BODY_PARTS" -> "Cơ thể"
    "FOOD" -> "Đồ ăn"
    "DAILY_LIFE" -> "Cuộc sống"
    "ANIMALS" -> "Động vật"
    "SCHOOL" -> "Trường học"
    "TRAVEL" -> "Du lịch"
    "WEATHER" -> "Thời tiết"
    "FAMILY" -> "Gia đình"
    "TECHNOLOGY" -> "Công nghệ"
    "CLOTHES" -> "Quần áo"
    "COLORS" -> "Màu sắc"
    "NUMBERS" -> "Số đếm"
    "COMMON_EXPRESSIONS" -> "Thành ngữ"
    "GREETINGS" -> "Chào hỏi"
    "TIME" -> "Thời gian"
    "NATURE" -> "Thiên nhiên"
    "SPORTS" -> "Thể thao"
    "MUSIC" -> "Âm nhạc"
    "WORK" -> "Công việc"
    "HEALTH" -> "Sức khỏe"
    "SHOPPING" -> "Mua sắm"
    else -> topic.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
private fun QuizTopicCard(
    topicInfo: QuizTopicInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = getQuizTopicColor(topicInfo.topic)

    Card(
        modifier = modifier.fillMaxWidth().height(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                Text(text = getQuizTopicEmoji(topicInfo.topic), fontSize = 32.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = getTopicDisplayName(topicInfo.topic),
                    style = AppTypography.BodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${topicInfo.quizCount} bộ đề",
                    style = AppTypography.LabelSmall,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QuizLevelSelection(
    topicName: String,
    levels: List<QuizTopicInfo>,
    onStartQuiz: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, "Quay lại", tint = AppColors.PrimaryLight)
            }
            Text(text = getQuizTopicEmoji(topicName), fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = getTopicDisplayName(topicName),
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "⚡ Chọn cấp độ JLPT",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (levels.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("N5", "N4", "N3", "N2", "N1").forEach { level ->
                    QuizJLPTLevelCard(
                        level = level,
                        quizCount = 0,
                        onClick = { onStartQuiz(level) }
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                levels.forEach { topicInfo ->
                    QuizJLPTLevelCard(
                        level = topicInfo.level,
                        quizCount = topicInfo.quizCount,
                        onClick = { onStartQuiz(topicInfo.level) }
                    )
                }
            }
        }
    }
}

private fun getQuizLevelColor(level: String): Color = when (level.uppercase()) {
    "N5" -> Color(0xFF22c55e)
    "N4" -> Color(0xFF84cc16)
    "N3" -> Color(0xFFeab308)
    "N2" -> Color(0xFFf97316)
    "N1" -> Color(0xFFef4444)
    else -> Color(0xFF6b7280)
}

private fun getQuizLevelBackgroundColor(level: String): Color = when (level.uppercase()) {
    "N5" -> Color(0xFFdcfce7)
    "N4" -> Color(0xFFecfccb)
    "N3" -> Color(0xFFfef9c3)
    "N2" -> Color(0xFFffedd5)
    "N1" -> Color(0xFFfee2e2)
    else -> Color(0xFFf3f4f6)
}

private fun getQuizDifficultyText(level: String): String = when (level.uppercase()) {
    "N5" -> "⭐"
    "N4" -> "⭐⭐"
    "N3" -> "⭐⭐⭐"
    "N2" -> "⭐⭐⭐⭐"
    "N1" -> "⭐⭐⭐⭐⭐"
    else -> "⭐"
}

private fun getQuizLevelDescription(level: String): String = when (level.uppercase()) {
    "N5" -> "Cơ bản"
    "N4" -> "Sơ cấp"
    "N3" -> "Trung cấp"
    "N2" -> "Trung cao cấp"
    "N1" -> "Cao cấp"
    else -> level
}

@Composable
private fun QuizJLPTLevelCard(
    level: String,
    quizCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levelColor = getQuizLevelColor(level)
    val backgroundColor = getQuizLevelBackgroundColor(level)

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = levelColor,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = level.uppercase(),
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getQuizLevelDescription(level),
                    style = AppTypography.BodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = getQuizDifficultyText(level), fontSize = 12.sp)
                    if (quizCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$quizCount bộ đề",
                            style = AppTypography.LabelSmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }

            Icon(
                Icons.Rounded.PlayCircle,
                contentDescription = "Bắt đầu ${level.uppercase()}",
                tint = levelColor,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
