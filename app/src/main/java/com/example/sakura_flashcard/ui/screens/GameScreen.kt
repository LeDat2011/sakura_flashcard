package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.data.api.LeaderboardEntry
import com.example.sakura_flashcard.data.model.*
import com.example.sakura_flashcard.ui.components.*
import com.example.sakura_flashcard.ui.games.*
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun GameScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gameStatistics by viewModel.gameStatistics.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = uiState) {
            is GameUiState.GameSelection -> {
                GameSelectionScreen(
                    availableGames = state.availableGames,
                    gameStatistics = gameStatistics,
                    leaderboard = leaderboard,
                    onGameSelect = { gameType, level ->
                        viewModel.startGame(gameType, level)
                    }
                )
            }
            is GameUiState.GameInProgress -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header với nút Back
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.backToGameSelection() }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Thoát game",
                                tint = AppColors.TextSecondary
                            )
                        }
                        
                        Text(
                            text = state.gameType.displayName,
                            style = AppTypography.TitleMedium,
                            color = AppColors.TextPrimary
                        )
                        
                        // Placeholder để cân bằng layout
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                    
                    // Game content
                    when (state.gameType) {
                        GameType.SENTENCE_ORDER_PUZZLE -> {
                            SentenceOrderPuzzleScreen(
                                gameState = state.gameState,
                                onOrderSubmit = { order ->
                                    viewModel.processGameInput(GameInput.WordOrder(order))
                                }
                            )
                        }
                        GameType.QUICK_ANSWER_CHALLENGE -> {
                            QuickAnswerChallengeScreen(
                                gameState = state.gameState,
                                onAnswerSelect = { answer ->
                                    viewModel.processGameInput(GameInput.Answer(answer))
                                }
                            )
                        }
                        GameType.MEMORY_MATCH_GAME -> {
                            MemoryMatchGameScreen(
                                gameState = state.gameState,
                                onCardFlip = { cardId ->
                                    viewModel.handleCardFlip(cardId)
                                }
                            )
                        }
                    }
                }
            }
            is GameUiState.GameCompleted -> {
                GameResultsDisplay(
                    gameResult = state.gameResult,
                    gameState = state.completedState,
                    onPlayAgain = {
                        viewModel.startGame(state.gameResult.gameType, state.gameResult.level)
                    },
                    onBackToMenu = {
                        viewModel.backToGameSelection()
                    }
                )
            }
            is GameUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.PrimaryLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Đang tải trò chơi...",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
            is GameUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = {
                        viewModel.backToGameSelection()
                    }
                )
            }
        }
    }
}

@Composable
private fun GameSelectionScreen(
    availableGames: List<GameType>,
    onGameSelect: (GameType, JLPTLevel) -> Unit,
    gameStatistics: GameStatistics? = null,
    leaderboard: List<LeaderboardEntry> = emptyList()
) {
    var selectedLevel by remember { mutableStateOf(JLPTLevel.N5) }
    var showStatistics by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with emoji and gradient feel
        Text(
            text = "🎮 Chọn trò chơi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Học tiếng Nhật qua các mini game thú vị!",
            style = AppTypography.BodyMedium,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Statistics and Leaderboard buttons - more colorful
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showStatistics = true },
                modifier = Modifier.weight(1f),
                enabled = gameStatistics != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C27B0),
                    contentColor = Color.White
                )
            ) {
                Text("📊 Thống kê")
            }
            
            Button(
                onClick = { showLeaderboard = true },
                modifier = Modifier.weight(1f),
                enabled = leaderboard.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                )
            ) {
                Text("🏆 Xếp hạng")
            }
        }

        // Level selection - colorful chips
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF3E5F5)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "⚡ Chọn cấp độ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B1FA2),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(JLPTLevel.values().toList()) { level ->
                        val levelColor = getGameLevelColor(level)
                        FilterChip(
                            onClick = { selectedLevel = level },
                            label = { 
                                Text(
                                    level.name,
                                    fontWeight = if (selectedLevel == level) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            selected = selectedLevel == level,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = levelColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Game selection - vertical list with colorful cards
        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableGames) { gameType ->
                GameCard(
                    gameType = gameType,
                    gameStatistics = gameStatistics?.gameTypeStats?.get(gameType),
                    onSelect = { onGameSelect(gameType, selectedLevel) }
                )
            }
        }
    }

    // Statistics Dialog
    if (showStatistics && gameStatistics != null) {
        StatisticsDialog(
            statistics = gameStatistics,
            onDismiss = { showStatistics = false }
        )
    }

    // Leaderboard Dialog
    if (showLeaderboard && leaderboard.isNotEmpty()) {
        LeaderboardDialog(
            leaderboard = leaderboard,
            onDismiss = { showLeaderboard = false }
        )
    }
}

// Colors for game levels
private fun getGameLevelColor(level: JLPTLevel): Color {
    return when (level) {
        JLPTLevel.N5 -> Color(0xFF4CAF50)
        JLPTLevel.N4 -> Color(0xFF8BC34A)
        JLPTLevel.N3 -> Color(0xFFFFC107)
        JLPTLevel.N2 -> Color(0xFFFF9800)
        JLPTLevel.N1 -> Color(0xFFE91E63)
    }
}

// Colors and icons for each game type
private fun getGameColor(gameType: GameType): Color {
    return when (gameType) {
        GameType.SENTENCE_ORDER_PUZZLE -> Color(0xFF2196F3) // Blue
        GameType.QUICK_ANSWER_CHALLENGE -> Color(0xFFE91E63) // Pink
        GameType.MEMORY_MATCH_GAME -> Color(0xFF4CAF50) // Green
    }
}

private fun getGameBackgroundColor(gameType: GameType): Color {
    return when (gameType) {
        GameType.SENTENCE_ORDER_PUZZLE -> Color(0xFFE3F2FD) // Light Blue
        GameType.QUICK_ANSWER_CHALLENGE -> Color(0xFFFCE4EC) // Light Pink
        GameType.MEMORY_MATCH_GAME -> Color(0xFFE8F5E9) // Light Green
    }
}

private fun getGameEmoji(gameType: GameType): String {
    return when (gameType) {
        GameType.SENTENCE_ORDER_PUZZLE -> "🧩"
        GameType.QUICK_ANSWER_CHALLENGE -> "⚡"
        GameType.MEMORY_MATCH_GAME -> "🃏"
    }
}

@Composable
private fun GameCard(
    gameType: GameType,
    gameStatistics: GameTypeStatistics? = null,
    onSelect: () -> Unit
) {
    val gameColor = getGameColor(gameType)
    val backgroundColor = getGameBackgroundColor(gameType)
    val emoji = getGameEmoji(gameType)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game emoji icon
            Surface(
                color = gameColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = emoji,
                        fontSize = 28.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gameType.displayName,
                    style = AppTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = gameColor
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = gameType.description,
                    style = AppTypography.BodySmall,
                    color = AppColors.TextSecondary,
                    maxLines = 2
                )
                
                // Game statistics preview
                gameStatistics?.let { stats ->
                    if (stats.gamesPlayed > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "🏆 ${stats.bestScore}",
                                style = AppTypography.LabelSmall,
                                color = gameColor,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "🎮 ${stats.gamesPlayed} lần",
                                style = AppTypography.LabelSmall,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }
            }
            
            // Play button
            Surface(
                color = gameColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "▶",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun SentenceOrderPuzzleScreen(
    gameState: GameState.InProgress,
    onOrderSubmit: (List<String>) -> Unit
) {
    val puzzle = gameState.currentQuestion as? SentencePuzzle
    
    if (puzzle != null) {
        Column {
            // Progress indicator
            GameProgressIndicator(
                current = gameState.questionsCompleted + 1,
                total = gameState.totalQuestions,
                score = gameState.currentScore
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SentenceOrderPuzzleComponent(
                words = puzzle.words,
                correctOrder = puzzle.correctOrder,
                onOrderSubmit = onOrderSubmit
            )
        }
    }
}

@Composable
private fun QuickAnswerChallengeScreen(
    gameState: GameState.InProgress,
    onAnswerSelect: (String) -> Unit
) {
    val question = gameState.currentQuestion as? QuickAnswerQuestion
    
    if (question != null) {
        Column {
            // Progress indicator
            GameProgressIndicator(
                current = gameState.questionsCompleted + 1,
                total = gameState.totalQuestions,
                score = gameState.currentScore
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            QuickAnswerChallengeComponent(
                question = question.question,
                options = question.options,
                correctAnswer = question.correctAnswer,
                timeRemaining = gameState.timeRemaining ?: 10L,
                onAnswerSelect = onAnswerSelect
            )
        }
    }
}

@Composable
private fun MemoryMatchGameScreen(
    gameState: GameState.InProgress,
    onCardFlip: (String) -> Unit
) {
    val cards = gameState.currentQuestion as? List<*>
    
    if (cards != null) {
        Column {
            // Progress indicator
            GameProgressIndicator(
                current = gameState.questionsCompleted,
                total = gameState.totalQuestions,
                score = gameState.currentScore
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            @Suppress("UNCHECKED_CAST")
            MemoryMatchGameComponent(
                cards = cards as List<MemoryCard>,
                onCardFlip = onCardFlip
            )
        }
    }
}

@Composable
private fun GameProgressIndicator(
    current: Int,
    total: Int,
    score: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tiến trình: $current/$total",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "Điểm: $score",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Lỗi",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onRetry) {
            Text("Thử lại")
        }
    }
}

@Composable
private fun StatisticsDialog(
    statistics: GameStatistics,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Thống kê trò chơi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Hiệu suất tổng thể",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            StatisticRow("Đã chơi", "${statistics.totalGamesPlayed}")
                            StatisticRow("Tổng điểm", "${statistics.totalScore}")
                            StatisticRow("Điểm trung bình", String.format("%.1f", statistics.getAverageScore()))
                            StatisticRow("Tổng thời gian", statistics.getTotalTimeFormatted())
                            StatisticRow("Độ chính xác TB", "${String.format("%.1f", statistics.averageAccuracy * 100)}%")
                        }
                    }
                }
                
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Điểm cao nhất",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            statistics.bestScores.forEach { (gameType, score) ->
                                StatisticRow(gameType.displayName, "$score")
                            }
                        }
                    }
                }
                
                items(statistics.gameTypeStats.toList()) { (gameType, stats) ->
                    if (stats.gamesPlayed > 0) {
                        Card {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = gameType.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                StatisticRow("Đã chơi", "${stats.gamesPlayed}")
                                StatisticRow("Điểm TB", String.format("%.1f", stats.averageScore))
                                StatisticRow("Điểm cao nhất", "${stats.bestScore}")
                                StatisticRow("TG trung bình", stats.getAverageTimeFormatted())
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun LeaderboardDialog(
    leaderboard: List<LeaderboardEntry>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Bảng xếp hạng",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(leaderboard) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (entry.rank) {
                                1 -> MaterialTheme.colorScheme.primaryContainer
                                2 -> MaterialTheme.colorScheme.secondaryContainer
                                3 -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${entry.rank}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                
                                Column {
                                    Text(
                                        text = entry.username,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = entry.gameType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            Text(
                                text = "${entry.score}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun StatisticRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}