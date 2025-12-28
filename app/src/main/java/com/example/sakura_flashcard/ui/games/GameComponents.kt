package com.example.sakura_flashcard.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.sakura_flashcard.data.model.GameResult
import com.example.sakura_flashcard.data.model.GameState
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

/**
 * Component for Sentence Order Puzzle game
 */
@Composable
fun SentenceOrderPuzzleComponent(
    words: List<String>,
    correctOrder: List<String>,
    onOrderSubmit: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWords by remember { mutableStateOf<List<String>>(emptyList()) }
    var availableWords by remember(words) { mutableStateOf(words.shuffled()) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instruction
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Text(
                text = "🧩 Sắp xếp các từ để tạo thành câu đúng",
                style = AppTypography.TitleMedium,
                color = Color(0xFF1565C0),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Selected words area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedWords.isEmpty()) {
                    Text(
                        text = "Chọn các từ bên dưới...",
                        color = AppColors.TextTertiary
                    )
                } else {
                    selectedWords.forEachIndexed { index, word ->
                        Surface(
                            onClick = {
                                selectedWords = selectedWords.toMutableList().apply { removeAt(index) }
                                availableWords = availableWords + word
                            },
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = word,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Available words
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            availableWords.forEach { word ->
                Surface(
                    onClick = {
                        selectedWords = selectedWords + word
                        availableWords = availableWords - word
                    },
                    color = Color(0xFF2196F3),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = word,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Submit button
        Button(
            onClick = { onOrderSubmit(selectedWords) },
            enabled = selectedWords.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kiểm tra", style = AppTypography.TitleMedium)
        }
        
        // Reset button
        TextButton(
            onClick = {
                selectedWords = emptyList()
                availableWords = words.shuffled()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = AppColors.TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Làm lại", color = AppColors.TextSecondary)
        }
    }
}

/**
 * Component for Quick Answer Challenge game
 */
@Composable
fun QuickAnswerChallengeComponent(
    question: String,
    options: List<String>,
    correctAnswer: String,
    timeRemaining: Long,
    onAnswerSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Timer
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (timeRemaining <= 3) Color(0xFFFFCDD2) else Color(0xFFE3F2FD)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (timeRemaining <= 3) Color(0xFFE53935) else Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${timeRemaining}s",
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (timeRemaining <= 3) Color(0xFFE53935) else Color(0xFF1565C0)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Question
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = question,
                style = AppTypography.HeadlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Options
        options.forEach { option ->
            val isSelected = selectedAnswer == option
            val backgroundColor = when {
                isSelected -> Color(0xFF2196F3)
                else -> Color.White
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { 
                        selectedAnswer = option
                        onAnswerSelect(option)
                    },
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = option,
                    style = AppTypography.BodyLarge,
                    color = if (isSelected) Color.White else AppColors.TextPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Component for Memory Match game
 */
@Composable
fun MemoryMatchGameComponent(
    cards: List<MemoryCard>,
    onCardFlip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = when {
        cards.size <= 8 -> 2
        cards.size <= 12 -> 3
        else -> 4
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instruction
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Text(
                text = "🃏 Tìm cặp thẻ khớp nhau",
                style = AppTypography.TitleMedium,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cards grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(400.dp)
        ) {
            items(cards) { card ->
                MemoryCardItem(
                    card = card,
                    onClick = { onCardFlip(card.id) }
                )
            }
        }
    }
}

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        card.isMatched -> Color(0xFFA5D6A7)
        card.isFlipped -> Color(0xFFFFFFFF)
        else -> Color(0xFF2196F3)
    }
    
    Card(
        modifier = modifier
            .aspectRatio(0.8f)
            .clickable(enabled = !card.isMatched && !card.isFlipped) { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFlipped || card.isMatched) {
                Text(
                    text = card.content,
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (card.isMatched) Color(0xFF1B5E20) else AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = "?",
                    style = AppTypography.HeadlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Component for displaying game results
 */
@Composable
fun GameResultsDisplay(
    gameResult: GameResult,
    gameState: GameState.Completed,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        Color(0xFFE8F5E9),
        Color(0xFFC8E6C9),
        Color(0xFFA5D6A7)
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Trophy icon
        Text(
            text = "🏆",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Congratulations
        Text(
            text = "Hoàn thành!",
            style = AppTypography.HeadlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = gameState.performance.getPerformanceRating(),
            style = AppTypography.TitleLarge,
            color = Color(0xFF388E3C)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Score card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResultRow(label = "Điểm số", value = "${gameResult.score}")
                Spacer(modifier = Modifier.height(12.dp))
                ResultRow(label = "Câu hỏi đúng", value = "${gameState.questionsCompleted}/${gameState.totalQuestions}")
                Spacer(modifier = Modifier.height(12.dp))
                ResultRow(label = "Độ chính xác", value = "${(gameState.performance.accuracy * 100).toInt()}%")
                Spacer(modifier = Modifier.height(12.dp))
                ResultRow(label = "Thời gian", value = "${gameResult.timeSpentSeconds}s")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quay lại")
            }
            
            Button(
                onClick = onPlayAgain,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chơi lại")
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = AppTypography.BodyLarge,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            style = AppTypography.TitleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
    }
}
