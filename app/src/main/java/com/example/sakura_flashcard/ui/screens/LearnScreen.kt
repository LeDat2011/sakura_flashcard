package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.ui.components.CharacterGrid
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onCharacterClick: (Character) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val tabs = listOf(
        CharacterScript.HIRAGANA,
        CharacterScript.KATAKANA,
        CharacterScript.KANJI
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.selectedScript) {
        val targetPage = tabs.indexOf(uiState.selectedScript)
        if (targetPage != -1 && targetPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < tabs.size) {
            viewModel.selectScript(tabs[pagerState.currentPage])
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.SurfaceMedium)
    ) {
        // Header
        Surface(
            color = AppColors.SurfaceLight,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📚", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Học chữ cái",
                        style = AppTypography.HeadlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.searchCharacters(it) },
                    placeholder = { Text("Tìm kiếm chữ cái...", color = AppColors.TextTertiary) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, "Tìm kiếm", tint = AppColors.TextSecondary)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(Icons.Rounded.Clear, "Xóa", tint = AppColors.TextSecondary)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryLight,
                        unfocusedBorderColor = AppColors.SurfaceBorder,
                        focusedContainerColor = AppColors.SurfaceLight,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                )

                // Tab Buttons - Cải thiện
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEachIndexed { index, script ->
                        val isSelected = pagerState.currentPage == index
                        val scriptColor = getScriptColor(script)
                        val bgColor = if (isSelected) scriptColor else getScriptBackgroundColor(script)

                        Surface(
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            color = bgColor,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = getScriptEmoji(script),
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = script.displayName,
                                    style = AppTypography.LabelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else scriptColor
                                )
                            }
                        }
                    }
                }
            }
        }

        // Content
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.PrimaryLight)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❌", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Lỗi tải chữ cái", style = AppTypography.TitleMedium, color = AppColors.DangerLight)
                        Text(uiState.error ?: "", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refreshData() },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Thử lại") }
                    }
                }
            }
            else -> {
                HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                    val script = tabs[page]
                    val characters = when (script) {
                        CharacterScript.HIRAGANA -> uiState.filteredHiraganaCharacters
                        CharacterScript.KATAKANA -> uiState.filteredKatakanaCharacters
                        CharacterScript.KANJI -> uiState.filteredKanjiCharacters
                    }
                    CharacterTabContent(
                        characters = characters,
                        script = script,
                        onCharacterClick = onCharacterClick,
                        searchQuery = uiState.searchQuery
                    )
                }
            }
        }
    }
}

// Colors cho từng loại chữ
private fun getScriptColor(script: CharacterScript): Color = when (script) {
    CharacterScript.HIRAGANA -> Color(0xFFE91E63) // Pink
    CharacterScript.KATAKANA -> Color(0xFF2196F3) // Blue
    CharacterScript.KANJI -> Color(0xFF4CAF50) // Green
}

private fun getScriptBackgroundColor(script: CharacterScript): Color = when (script) {
    CharacterScript.HIRAGANA -> Color(0xFFFCE4EC) // Light Pink
    CharacterScript.KATAKANA -> Color(0xFFE3F2FD) // Light Blue
    CharacterScript.KANJI -> Color(0xFFE8F5E9) // Light Green
}

private fun getScriptEmoji(script: CharacterScript): String = when (script) {
    CharacterScript.HIRAGANA -> "あ"
    CharacterScript.KATAKANA -> "ア"
    CharacterScript.KANJI -> "漢"
}

@Composable
private fun CharacterTabContent(
    characters: List<Character>,
    script: CharacterScript,
    onCharacterClick: (Character) -> Unit,
    searchQuery: String = "",
    modifier: Modifier = Modifier
) {
    val scriptColor = getScriptColor(script)
    val backgroundColor = getScriptBackgroundColor(script)

    Column(modifier = modifier.fillMaxSize()) {
        // Info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = scriptColor, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(getScriptEmoji(script), style = AppTypography.TitleLarge, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(script.displayName, style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = scriptColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(script.description, style = AppTypography.BodySmall, color = AppColors.TextSecondary)
                }
            }
        }

        // Character count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${characters.size} chữ cái", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
            if (searchQuery.isNotEmpty()) {
                Surface(color = AppColors.PrimaryLightest, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        "Lọc: \"$searchQuery\"",
                        style = AppTypography.LabelSmall,
                        color = AppColors.PrimaryLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Character Grid
        if (characters.isNotEmpty()) {
            CharacterGrid(
                characters = characters,
                scriptColor = scriptColor,
                onCharacterClick = onCharacterClick,
                modifier = Modifier.weight(1f)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Không tìm thấy kết quả", style = AppTypography.TitleMedium, color = AppColors.TextSecondary)
                }
            }
        }
    }
}