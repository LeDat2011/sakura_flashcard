package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.data.model.QuizSession
import com.example.sakura_flashcard.ui.components.QuizProgressIndicator
import com.example.sakura_flashcard.ui.components.QuizQuestionCard
import com.example.sakura_flashcard.ui.components.QuizTimer
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSessionScreen(
    topic: String,
    level: String,
    onNavigateBack: () -> Unit,
    onNavigateToResults: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuizSessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(topic, level) {
        viewModel.startQuiz(topic, level)
    }

    Scaffold(
        topBar = {
            Surface(
                color = AppColors.SurfaceLight,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Thoát",
                            tint = AppColors.TextPrimary
                        )
                    }

                    Text(
                        text = "Kiểm tra",
                        style = AppTypography.TitleLarge,
                        color = AppColors.TextPrimary
                    )

                    // Timer placeholder
                    if (uiState is QuizSessionUiState.InProgress) {
                        QuizTimer(
                            timeRemainingSeconds = (uiState as QuizSessionUiState.InProgress).timeRemainingSeconds
                        )
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }
            }
        },
        containerColor = AppColors.BackgroundPrimary
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is QuizSessionUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AppColors.PrimaryLight)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Đang tải câu hỏi...",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                is QuizSessionUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "😕",
                            style = AppTypography.HeadlineLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            style = AppTypography.TitleMedium,
                            color = AppColors.TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.PrimaryLight
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Quay lại")
                        }
                    }
                }

                is QuizSessionUiState.InProgress -> {
                    QuizInProgressContent(
                        quizSession = state.quizSession,
                        userAnswer = state.currentAnswer,
                        timeRemaining = state.timeRemainingSeconds,
                        onAnswerSelected = viewModel::selectAnswer,
                        onSubmitAnswer = viewModel::submitAnswer
                    )
                }

                is QuizSessionUiState.Completed -> {
                    LaunchedEffect(state.quizResult.id) {
                        onNavigateToResults(state.quizResult.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizInProgressContent(
    quizSession: QuizSession,
    userAnswer: String,
    timeRemaining: Int,
    onAnswerSelected: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentQuestion = quizSession.questions[quizSession.currentQuestionIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress
        QuizProgressIndicator(
            currentQuestion = quizSession.currentQuestionIndex + 1,
            totalQuestions = quizSession.questions.size
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question Card
        QuizQuestionCard(
            question = currentQuestion,
            currentQuestionIndex = quizSession.currentQuestionIndex,
            totalQuestions = quizSession.questions.size,
            userAnswer = userAnswer,
            onAnswerSelected = onAnswerSelected,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = onSubmitAnswer,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = userAnswer.isNotBlank(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryLight,
                disabledContainerColor = AppColors.SurfaceBorder
            )
        ) {
            Text(
                text = if (quizSession.currentQuestionIndex == quizSession.questions.size - 1)
                    "Hoàn thành" else "Tiếp theo",
                style = AppTypography.TitleMedium
            )
        }
    }
}

sealed class QuizSessionUiState {
    object Loading : QuizSessionUiState()
    data class Error(val message: String) : QuizSessionUiState()
    data class InProgress(
        val quizSession: QuizSession,
        val currentAnswer: String = "",
        val timeRemainingSeconds: Int = 600
    ) : QuizSessionUiState()
    data class Completed(val quizResult: QuizResult) : QuizSessionUiState()
}