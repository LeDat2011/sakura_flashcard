package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDeckManagerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val customDecks by viewModel.customDecks.collectAsState()
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var showAddFlashcardDialog by remember { mutableStateOf<String?>(null) } // deckId
    var deckToDelete by remember { mutableStateOf<CustomDeck?>(null) }
    var expandedDeckId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            
            Text(
                text = "Flashcard của bạn",
                style = AppTypography.HeadlineSmall,
                color = AppColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create New Deck Button
        Button(
            onClick = { showCreateDeckDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tạo flashcard mới", style = AppTypography.TitleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Decks List
        if (customDecks.isEmpty()) {
            EmptyDecksState(onCreateDeck = { showCreateDeckDialog = true })
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(customDecks) { deck ->
                    CustomDeckCard(
                        deck = deck,
                        isExpanded = expandedDeckId == deck.id,
                        onToggleExpand = {
                            expandedDeckId = if (expandedDeckId == deck.id) null else deck.id
                        },
                        onAddFlashcard = { showAddFlashcardDialog = deck.id },
                        onDeleteFlashcard = { flashcardId ->
                            viewModel.removeFlashcardFromDeck(deck.id, flashcardId)
                        },
                        onDelete = { deckToDelete = deck }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showCreateDeckDialog) {
        CreateDeckDialog(
            onDismiss = { showCreateDeckDialog = false },
            onConfirm = { name ->
                viewModel.createCustomDeck(name)
                showCreateDeckDialog = false
            }
        )
    }

    showAddFlashcardDialog?.let { deckId ->
        AddFlashcardDialog(
            onDismiss = { showAddFlashcardDialog = null },
            onConfirm = { japanese, romaji, vietnamese ->
                viewModel.addFlashcardToDeck(deckId, japanese, romaji, vietnamese)
                showAddFlashcardDialog = null
            }
        )
    }

    deckToDelete?.let { deck ->
        DeleteDeckDialog(
            deckName = deck.name,
            onDismiss = { deckToDelete = null },
            onConfirm = {
                viewModel.deleteCustomDeck(deck.id)
                deckToDelete = null
            }
        )
    }
}

@Composable
private fun CustomDeckCard(
    deck: CustomDeck,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onAddFlashcard: () -> Unit,
    onDeleteFlashcard: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        border = BorderStroke(1.dp, AppColors.SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deck.name,
                        style = AppTypography.TitleMedium,
                        color = AppColors.TextPrimary
                    )
                    Text(
                        text = "${deck.flashcardCount} thẻ",
                        style = AppTypography.BodySmall,
                        color = AppColors.TextSecondary
                    )
                }

                Row {
                    IconButton(onClick = onToggleExpand) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                            tint = AppColors.TextSecondary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = AppColors.DangerLight
                        )
                    }
                }
            }

            // Add flashcard button
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAddFlashcard,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AppColors.PrimaryLight)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AppColors.PrimaryLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm thẻ mới", color = AppColors.PrimaryLight)
            }

            // Expanded flashcard list
            if (isExpanded && deck.flashcards.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = AppColors.SurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))
                
                deck.flashcards.forEach { flashcard ->
                    FlashcardItem(
                        flashcard = flashcard,
                        onDelete = { onDeleteFlashcard(flashcard.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun FlashcardItem(
    flashcard: CustomFlashcard,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.SurfaceMedium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flashcard.japanese,
                    style = AppTypography.TitleMedium,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = flashcard.romaji,
                    style = AppTypography.BodySmall,
                    color = AppColors.PrimaryLight
                )
                Text(
                    text = flashcard.vietnamese,
                    style = AppTypography.BodyMedium,
                    color = AppColors.TextSecondary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Xóa thẻ",
                    tint = AppColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun AddFlashcardDialog(
    onDismiss: () -> Unit,
    onConfirm: (japanese: String, romaji: String, vietnamese: String) -> Unit
) {
    var japanese by remember { mutableStateOf("") }
    var romaji by remember { mutableStateOf("") }
    var vietnamese by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm thẻ mới", style = AppTypography.TitleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = japanese,
                    onValueChange = { japanese = it },
                    label = { Text("Từ tiếng Nhật") },
                    placeholder = { Text("Ví dụ: ありがとう") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = romaji,
                    onValueChange = { romaji = it },
                    label = { Text("Phiên âm Romaji") },
                    placeholder = { Text("Vi du: arigatou") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = vietnamese,
                    onValueChange = { vietnamese = it },
                    label = { Text("Nghĩa tiếng Việt") },
                    placeholder = { Text("Ví dụ: Cảm ơn") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(japanese, romaji, vietnamese) },
                enabled = japanese.isNotBlank() && romaji.isNotBlank() && vietnamese.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun EmptyDecksState(onCreateDeck: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = AppColors.TextTertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có bộ thẻ nào",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tạo bộ thẻ đầu tiên để bắt đầu học từ vựng của riêng bạn",
            style = AppTypography.BodyMedium,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
private fun CreateDeckDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var deckName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo bộ thẻ mới") },
        text = {
            OutlinedTextField(
                value = deckName,
                onValueChange = { deckName = it },
                label = { Text("Tên bộ thẻ") },
                placeholder = { Text("Ví dụ: Từ vựng Anime") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(deckName) },
                enabled = deckName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
            ) {
                Text("Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
private fun DeleteDeckDialog(deckName: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa bộ thẻ") },
        text = { Text("Bạn có chắc muốn xóa \"$deckName\"? Hành động này không thể hoàn tác.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.DangerLight)
            ) {
                Text("Xóa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}