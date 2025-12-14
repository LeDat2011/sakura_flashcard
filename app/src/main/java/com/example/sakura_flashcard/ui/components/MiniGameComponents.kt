package com.example.sakura_flashcard.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.ui.games.MemoryCard
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import kotlinx.coroutines.delay

// ==================== SENTENCE ORDER PUZZLE - ĐÃ SỬA ====================
@Composable
fun SentenceOrderPuzzleComponent(
    words: List<String>,
    correctOrder: List<String>,
    onOrderSubmit: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWords by remember(words) { mutableStateOf<List<String>>(emptyList()) }
    var availableWords by remember(words) { mutableStateOf(words.shuffled()) }
    var showResult by remember(words) { mutableStateOf(false) }
    var isCorrect by remember(words) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sắp xếp các từ thành câu đúng",
            style = AppTypography.TitleMedium,
            textAlign = TextAlign.Center,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // === BLANK AREA - Hiển thị như một câu dài ===
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (showResult) {
                    if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                } else Color(0xFFF5F5F5)
            ),
            border = BorderStroke(
                2.dp,
                if (showResult) {
                    if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                } else AppColors.SurfaceBorder
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = if (selectedWords.isEmpty()) Alignment.Center else Alignment.TopStart
            ) {
                if (selectedWords.isEmpty()) {
                    Text(
                        text = "Nhấn vào các từ bên dưới để ghép câu",
                        style = AppTypography.BodyMedium,
                        color = AppColors.TextTertiary
                    )
                } else {
                    // === SỬA: Hiển thị như một dòng với FlowRow ===
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedWords.forEachIndexed { index, word ->
                            SelectedWordChip(
                                word = word,
                                enabled = !showResult,
                                onClick = {
                                    if (!showResult) {
                                        selectedWords = selectedWords.toMutableList().apply {
                                            removeAt(index)
                                        }
                                        availableWords = availableWords + word
                                    }
                                }
                            )
                            // Thêm khoảng cách giữa các từ
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // === WORD BANK - Các từ chưa chọn ===
        if (availableWords.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableWords.forEachIndexed { index, word ->
                    AvailableWordChip(
                        word = word,
                        enabled = !showResult,
                        onClick = {
                            if (!showResult) {
                                selectedWords = selectedWords + word
                                availableWords = availableWords.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        } else if (!showResult) {
            Text(
                text = "Đã chọn tất cả từ",
                style = AppTypography.BodyMedium,
                color = AppColors.TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // === KẾT QUẢ ===
        if (showResult) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isCorrect) "✓ Chính xác!" else "✗ Chưa đúng!",
                        style = AppTypography.TitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )

                    if (!isCorrect) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đáp án đúng:", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
                        Text(
                            text = correctOrder.joinToString(" "),
                            style = AppTypography.BodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.PrimaryLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onOrderSubmit(selectedWords) },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tiếp tục", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
        } else {
            Button(
                onClick = {
                    isCorrect = selectedWords == correctOrder
                    showResult = true
                },
                enabled = selectedWords.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.PrimaryLight,
                    disabledContainerColor = AppColors.SurfaceBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Kiểm tra", modifier = Modifier.padding(vertical = 4.dp), style = AppTypography.TitleMedium)
            }
        }
    }
}

@Composable
private fun SelectedWordChip(word: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = AppColors.PrimaryLight,
        shadowElevation = 2.dp
    ) {
        Text(
            text = word,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = AppTypography.BodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
private fun AvailableWordChip(word: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .border(1.dp, AppColors.SurfaceBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.SurfaceLight,
        shadowElevation = 2.dp
    ) {
        Text(
            text = word,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = AppTypography.BodyLarge,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )
    }
}

// ==================== QUICK ANSWER CHALLENGE - ĐÃ SỬA ====================
@Composable
fun QuickAnswerChallengeComponent(
    question: String,
    options: List<String>,
    correctAnswer: String,
    timeRemaining: Long,
    onAnswerSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnswer by remember(question) { mutableStateOf<String?>(null) }
    var showResult by remember(question) { mutableStateOf(false) }
    var isTimeUp by remember(question) { mutableStateOf(false) } // === SỬA: Xử lý khi hết thời gian - chỉ hiển thị, không crash ===
    // === Xử lý khi hết thời gian ===
    LaunchedEffect(timeRemaining, question) {
        if (timeRemaining <= 0 && !showResult && !isTimeUp) {
            isTimeUp = true
            showResult = true
            selectedAnswer = null // Không chọn đáp án nào
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer - chỉ hiển thị khi chưa có kết quả
        if (!showResult) {
            TimerDisplay(
                timeRemaining = timeRemaining,
                totalTime = 10L,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = question,
                style = AppTypography.HeadlineSmall,
                textAlign = TextAlign.Center,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showResult) {
            val isCorrect = selectedAnswer == correctAnswer

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isTimeUp -> Color(0xFFFFF3E0) // Orange cho hết giờ
                        isCorrect -> Color(0xFFE8F5E9) // Xanh cho đúng
                        else -> Color(0xFFFFEBEE) // Đỏ cho sai
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when {
                            isTimeUp -> "⏰ Hết giờ!"
                            isCorrect -> "✓ Chính xác!"
                            else -> "✗ Sai rồi!"
                        },
                        style = AppTypography.TitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isTimeUp -> Color(0xFFFF9800)
                            isCorrect -> Color(0xFF4CAF50)
                            else -> Color(0xFFF44336)
                        }
                    )
                    
                    // Hiển thị đáp án đúng nếu sai hoặc hết giờ
                    if (!isCorrect || isTimeUp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Đáp án đúng: $correctAnswer",
                            style = AppTypography.BodyLarge,
                            color = AppColors.PrimaryLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút tiếp tục
            Button(
                onClick = { 
                    onAnswerSelect(selectedAnswer ?: "") 
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Câu tiếp theo →", style = AppTypography.TitleMedium)
            }
        } else {
            // Các options - chỉ hiện khi chưa có kết quả
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            selectedAnswer = option
                            showResult = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = option,
                            style = AppTypography.BodyLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerDisplay(timeRemaining: Long, totalTime: Long, modifier: Modifier = Modifier) {
    val progress = (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)
    val color by animateColorAsState(
        targetValue = when {
            progress > 0.6f -> Color(0xFF4CAF50)
            progress > 0.3f -> Color(0xFFFFC107)
            else -> Color(0xFFF44336)
        },
        label = "timer_color"
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${timeRemaining}s",
            style = AppTypography.HeadlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(200.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = AppColors.SurfaceBorder
        )
    }
}

// ==================== MEMORY MATCH GAME - ĐÃ SỬA ====================
@Composable
fun MemoryMatchGameComponent(
    cards: List<MemoryCard>,
    onCardFlip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(cards, key = { it.id }) { card ->
            FlipCard(
                card = card,
                onFlip = {
                    // === SỬA: Chỉ cho flip nếu card chưa flip và chưa match ===
                    if (!card.isFlipped && !card.isMatched) {
                        onCardFlip(card.id)
                    }
                }
            )
        }
    }
}

@Composable
private fun FlipCard(card: MemoryCard, onFlip: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(400),
        label = "card_flip"
    )

    val cardColor = when {
        card.isMatched -> Color(0xFFE8F5E9) // Màu xanh nhạt khi match
        card.isFlipped -> Color(0xFFE3F2FD) // Màu xanh dương nhạt khi flip
        else -> Color(0xFFF5F5F5) // Màu xám nhạt khi úp
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = !card.isMatched && !card.isFlipped) { onFlip() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (card.isMatched) BorderStroke(2.dp, Color(0xFF4CAF50)) else null
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                // Mặt sau - dấu hỏi
                Text(
                    text = "?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextTertiary
                )
            } else {
                // Mặt trước - nội dung
                Text(
                    text = card.content,
                    style = AppTypography.BodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (card.isMatched) Color(0xFF4CAF50) else AppColors.TextPrimary,
                    modifier = Modifier
                        .padding(4.dp)
                        .graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}

// ==================== GAME RESULTS ====================
@Composable
fun GameResultsDisplay(
    gameResult: GameResult,
    gameState: GameState.Completed,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎉", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hoàn thành!",
            style = AppTypography.HeadlineSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryLight
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ResultRow("Điểm số", "${gameState.finalScore}")
                ResultRow("Thời gian", "${gameState.totalTime}s")
                ResultRow("Độ chính xác", "${String.format("%.1f", gameState.performance.accuracy * 100)}%")
                ResultRow("Hoàn thành", "${gameState.questionsCompleted}/${gameState.totalQuestions}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Quay lại")
            }
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Chơi lại")
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
        Text(value, style = AppTypography.BodyMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
    }
}

// FlowRow helper - nếu chưa có thì dùng cái này
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
