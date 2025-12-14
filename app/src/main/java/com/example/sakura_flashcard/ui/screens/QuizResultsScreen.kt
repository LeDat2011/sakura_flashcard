package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.model.QuizAnswer
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultsScreen(
    resultId: String,
    onNavigateBack: () -> Unit,
    onRetakeQuiz: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(resultId) {
        viewModel.loadQuizResult(resultId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kết quả bài kiểm tra",
                        style = AppTypography.TitleLarge,
                        color = AppColors.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, "Quay lại", tint = AppColors.PrimaryLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.SurfaceMedium)
            )
        },
        containerColor = AppColors.SurfaceMedium
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is QuizResultsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppColors.PrimaryLight
                    )
                }

                is QuizResultsUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "❌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Lỗi tải kết quả",
                            style = AppTypography.HeadlineSmall,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            state.message,
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Quay lại", color = Color.White)
                        }
                    }
                }

                is QuizResultsUiState.Success -> {
                    QuizResultsContent(
                        quizResult = state.quizResult,
                        showExplanations = state.showExplanations,
                        onRetakeQuiz = {
                            onRetakeQuiz(
                                state.quizResult.topic.name,
                                state.quizResult.level.name
                            )
                        },
                        onToggleExplanations = viewModel::toggleExplanations,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizResultsContent(
    quizResult: QuizResult,
    showExplanations: Boolean,
    onRetakeQuiz: () -> Unit,
    onToggleExplanations: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scorePercent = (quizResult.score.toFloat() / quizResult.totalQuestions * 100).toInt()
    val isPassing = scorePercent >= 70

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPassing) Color(0xFFd1fae5) else Color(0xFFfee2e2)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isPassing) "🎉" else "😢",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isPassing) "Chúc mừng!" else "Cố gắng lên!",
                        style = AppTypography.HeadlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isPassing) Color(0xFF059669) else Color(0xFFdc2626)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${quizResult.score}/${quizResult.totalQuestions} câu đúng",
                        style = AppTypography.TitleLarge,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "$scorePercent%",
                        style = AppTypography.HeadlineLarge,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPassing) Color(0xFF059669) else Color(0xFFdc2626)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thời gian: ${quizResult.timeSpentSeconds}s",
                        style = AppTypography.BodyMedium,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleExplanations,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AppColors.PrimaryLight)
                ) {
                    Icon(
                        if (showExplanations) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = AppColors.PrimaryLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (showExplanations) "Ẩn chi tiết" else "Xem chi tiết",
                        color = AppColors.PrimaryLight
                    )
                }

                Button(
                    onClick = onRetakeQuiz,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Làm lại")
                }
            }
        }

        // Questions Review
        if (showExplanations) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("📝", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Xem lại câu hỏi",
                        style = AppTypography.TitleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
            }

            itemsIndexed(quizResult.answers) { index, answer ->
                QuestionReviewCard(
                    questionNumber = index + 1,
                    answer = answer
                )
            }
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                border = BorderStroke(1.dp, AppColors.SurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Tổng kết",
                            style = AppTypography.TitleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    }

                    val incorrectCount = quizResult.answers.count { !it.isCorrect }
                    if (incorrectCount > 0) {
                        Text(
                            "Bạn trả lời sai $incorrectCount câu. Hãy ôn tập thêm nhé!",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                    } else {
                        Surface(
                            color = Color(0xFFd1fae5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "🎉 Tuyệt vời! Bạn đã trả lời đúng tất cả câu hỏi!",
                                style = AppTypography.BodyMedium,
                                color = Color(0xFF059669),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuestionReviewCard(
    questionNumber: Int,
    answer: QuizAnswer,
    modifier: Modifier = Modifier
) {
    val isCorrect = answer.isCorrect
    val bgColor = if (isCorrect) Color(0xFFd1fae5) else Color(0xFFfee2e2)
    val accentColor = if (isCorrect) Color(0xFF059669) else Color(0xFFdc2626)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        if (isCorrect) Icons.Rounded.Check else Icons.Rounded.Close,
                        if (isCorrect) "Đúng" else "Sai",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
                Text(
                    "Câu hỏi $questionNumber",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row {
                    Text(
                        "Câu trả lời của bạn: ",
                        style = AppTypography.BodyMedium,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        answer.userAnswer.ifEmpty { "(Chưa trả lời)" },
                        style = AppTypography.BodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isCorrect) Color(0xFF059669) else Color(0xFFdc2626)
                    )
                    if (isCorrect) {
                        Text(" ✓", color = Color(0xFF059669))
                    }
                }

                if (!isCorrect) {
                    Row {
                        Text(
                            "Đáp án đúng: ",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                        Text(
                            answer.correctAnswer,
                            style = AppTypography.BodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                }
            }
        }
    }
}