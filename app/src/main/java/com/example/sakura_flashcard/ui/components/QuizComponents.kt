package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.QuizQuestion
import com.example.sakura_flashcard.data.model.QuestionType
import com.example.sakura_flashcard.data.model.QuizResult
import com.example.sakura_flashcard.ui.theme.AccessibilityUtils
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    userAnswer: String,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val questionNumber = currentQuestionIndex + 1

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFf0f9ff), Color(0xFFe0f2fe))
                    )
                )
                .padding(24.dp)
        ) {
            // Question number badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.PrimaryLight
            ) {
                Text(
                    text = "Câu $questionNumber/$totalQuestions",
                    style = AppTypography.LabelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Question content
            Text(
                text = question.content,
                style = AppTypography.HeadlineSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                ),
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Answer options based on question type
            when (question.type) {
                QuestionType.MULTIPLE_CHOICE -> {
                    MultipleChoiceOptions(
                        options = question.options ?: emptyList(),
                        selectedAnswer = userAnswer,
                        onAnswerSelected = onAnswerSelected
                    )
                }
                QuestionType.FILL_IN_BLANK -> {
                    FillInBlankInput(
                        userAnswer = userAnswer,
                        onAnswerChanged = onAnswerSelected
                    )
                }
                QuestionType.TRUE_FALSE -> {
                    TrueFalseOptions(
                        selectedAnswer = userAnswer,
                        onAnswerSelected = onAnswerSelected
                    )
                }
            }
        }
    }
}

@Composable
fun QuizProgressIndicator(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiến độ",
                style = AppTypography.LabelSmall,
                color = AppColors.TextSecondary
            )
            Text(
                text = "$currentQuestion/$totalQuestions",
                style = AppTypography.LabelSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryLight
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { currentQuestion.toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = AppColors.PrimaryLight,
            trackColor = AppColors.PrimaryLightest
        )
    }
}

@Composable
fun MultipleChoiceOptions(
    options: List<String>,
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selectedAnswer == option
            val optionLetter = ('A' + index).toString()

            AnswerOptionCard(
                letter = optionLetter,
                text = option,
                isSelected = isSelected,
                onClick = { onAnswerSelected(option) }
            )
        }
    }
}

@Composable
private fun AnswerOptionCard(
    letter: String,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.PrimaryLightest else AppColors.SurfaceLight,
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AppColors.PrimaryLight else AppColors.SurfaceBorder,
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter badge
            Surface(
                shape = CircleShape,
                color = if (isSelected) AppColors.PrimaryLight else AppColors.SurfaceMedium,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = letter,
                        style = AppTypography.TitleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else AppColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = AppTypography.BodyLarge,
                color = AppColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Đã chọn",
                    tint = AppColors.PrimaryLight,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FillInBlankInput(
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = userAnswer,
        onValueChange = onAnswerChanged,
        label = { Text("Nhập câu trả lời") },
        placeholder = { Text("Điền đáp án của bạn...") },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.PrimaryLight,
            unfocusedBorderColor = AppColors.SurfaceBorder,
            focusedContainerColor = AppColors.SurfaceLight,
            unfocusedContainerColor = AppColors.SurfaceLight
        ),
        singleLine = true
    )
}

@Composable
fun TrueFalseOptions(
    selectedAnswer: String,
    onAnswerSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        listOf("Đúng" to "True", "Sai" to "False").forEach { (label, value) ->
            val isSelected = selectedAnswer == value

            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) {
                            if (value == "True") AppColors.SuccessLight else AppColors.DangerLight
                        } else AppColors.SurfaceBorder,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        if (value == "True") AppColors.SuccessLightest else AppColors.DangerLightest
                    } else AppColors.SurfaceLight
                ),
                onClick = { onAnswerSelected(value) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = AppTypography.TitleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) {
                            if (value == "True") AppColors.SuccessLight else AppColors.DangerLight
                        } else AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun QuizTimer(
    timeRemainingSeconds: Int,
    modifier: Modifier = Modifier
) {
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeText = String.format("%02d:%02d", minutes, seconds)
    val isUrgent = timeRemainingSeconds <= 30

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isUrgent) AppColors.DangerLightest else AppColors.SurfaceLight,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = if (isUrgent) AppColors.DangerLight else AppColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeText,
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isUrgent) AppColors.DangerLight else AppColors.TextPrimary
            )
        }
    }
}

@Composable
fun QuizResultsCard(
    quizResult: QuizResult,
    onRetakeQuiz: () -> Unit,
    onViewExplanations: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentageScore = quizResult.getPercentageScore().toInt()
    val isPassing = quizResult.isPassing()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isPassing) {
                            listOf(Color(0xFFecfdf5), Color(0xFFd1fae5))
                        } else {
                            listOf(Color(0xFFfef2f2), Color(0xFFfecaca))
                        }
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Result emoji
            Text(
                text = if (isPassing) "🎉" else "💪",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Score
            Text(
                text = "$percentageScore%",
                style = AppTypography.HeadlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isPassing) AppColors.SuccessLight else AppColors.DangerLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${quizResult.score}/${quizResult.totalQuestions} câu đúng",
                style = AppTypography.TitleMedium,
                color = AppColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isPassing) "Xuất sắc! Tiếp tục phát huy nhé!" else "Cố gắng thêm nhé! Bạn có thể làm được!",
                style = AppTypography.BodyMedium,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewExplanations,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.PrimaryLight
                    )
                ) {
                    Text("Xem giải thích")
                }

                Button(
                    onClick = onRetakeQuiz,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.PrimaryLight
                    )
                ) {
                    Text("Làm lại")
                }
            }
        }
    }
}