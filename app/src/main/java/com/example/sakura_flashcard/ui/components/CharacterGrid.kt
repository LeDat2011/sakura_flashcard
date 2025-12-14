package com.example.sakura_flashcard.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import java.util.Locale

@Composable
fun CharacterGrid(
    characters: List<Character>,
    onCharacterClick: (Character) -> Unit,
    modifier: Modifier = Modifier,
    scriptColor: Color = AppColors.PrimaryLight // Thêm tham số màu
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPANESE
                isTtsReady = true
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        items(characters) { character ->
            CharacterGridItem(
                character = character,
                scriptColor = scriptColor,
                onClick = { onCharacterClick(character) },
                onPlayPronunciation = {
                    if (isTtsReady) {
                        tts?.speak(character.character, TextToSpeech.QUEUE_FLUSH, null, character.id)
                    }
                }
            )
        }
    }
}

@Composable
private fun CharacterGridItem(
    character: Character,
    scriptColor: Color,
    onClick: () -> Unit,
    onPlayPronunciation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tạo màu nền nhạt từ màu chính
    val bgColor = scriptColor.copy(alpha = 0.1f)

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Character - Chữ cái lớn
            Text(
                text = character.character,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = scriptColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Romaji
            Text(
                text = character.pronunciation.firstOrNull() ?: "",
                style = AppTypography.BodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Play button
            Surface(
                onClick = onPlayPronunciation,
                color = scriptColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Phát âm",
                    tint = scriptColor,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                )
            }
        }
    }
}