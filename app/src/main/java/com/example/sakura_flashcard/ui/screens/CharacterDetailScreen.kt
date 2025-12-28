package com.example.sakura_flashcard.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sakura_flashcard.data.model.Point
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakura_flashcard.data.model.Character
import com.example.sakura_flashcard.data.model.CharacterScript
import com.example.sakura_flashcard.data.model.Stroke
import com.example.sakura_flashcard.data.model.StrokeDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sử dụng characterId là chính chữ Hiragana được truyền từ navigation
    val character = remember(characterId) {
        Character(
            id = characterId,
            character = characterId, // characterId bây giờ là chữ Hiragana thực tế (あ, い, う, ...)
            script = CharacterScript.HIRAGANA,
            pronunciation = listOf(getHiraganaPronunciation(characterId)),
            strokeOrder = listOf(
                Stroke(
                    order = 1, 
                    points = listOf(Point(10f, 10f), Point(50f, 10f)), 
                    direction = StrokeDirection.HORIZONTAL,
                    path = "M 10 10 L 50 10"
                )
            ),
            examples = listOf()
        )
    }
    var isAnimationPlaying by remember { mutableStateOf(false) }
    var currentStrokeIndex by remember { mutableStateOf(0) }
    
    val animationProgress by animateFloatAsState(
        targetValue = if (isAnimationPlaying) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearEasing
        ),
        finishedListener = {
            if (isAnimationPlaying && currentStrokeIndex < character.strokeOrder.size - 1) {
                currentStrokeIndex++
            } else if (isAnimationPlaying) {
                isAnimationPlaying = false
                currentStrokeIndex = 0
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Character Details") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        // Character Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large character display
                Text(
                    text = character.character,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Script type
                Text(
                    text = character.script.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Stroke Order Image from PDF
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)) // Light pink background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✍️ Hướng dẫn viết",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFBE185D) // Pink color for title
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Hiển thị hình ảnh stroke order từ PDF
                val strokeResourceId = com.example.sakura_flashcard.ui.components.StrokeOrderHelper.getStrokeOrderResource(character.character)
                
                if (strokeResourceId != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = strokeResourceId),
                        contentDescription = "Stroke order for ${character.character}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    // Fallback: Hiển thị chữ lớn nếu không có hình
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(Color(0xFFFCE7F3), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.character,
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBE185D)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pronunciation Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Pronunciation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                character.pronunciation.forEach { pronunciation ->
                    Text(
                        text = pronunciation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
        
        // Example Vocabulary Section
        if (character.hasExamples()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Example Vocabulary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    character.examples.forEach { example ->
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StrokeOrderCanvas(
    strokes: List<Stroke>,
    currentStrokeIndex: Int,
    animationProgress: Float,
    modifier: Modifier = Modifier
) {
    val strokeColor = MaterialTheme.colorScheme.primary
    val guideColor = MaterialTheme.colorScheme.outline
    
    Canvas(
        modifier = modifier
            .drawBehind {
                // Draw background grid for reference
                drawGrid(this, guideColor)
            }
    ) {
        val canvasSize = size.minDimension
        val strokeWidth = canvasSize * 0.02f
        
        // Draw completed strokes
        for (i in 0 until currentStrokeIndex) {
            drawStroke(strokes[i], strokeColor, strokeWidth, 1f)
        }
        
        // Draw current animating stroke
        if (currentStrokeIndex < strokes.size) {
            drawStroke(strokes[currentStrokeIndex], strokeColor, strokeWidth, animationProgress)
        }
        
        // Draw remaining strokes as guides
        for (i in currentStrokeIndex + 1 until strokes.size) {
            drawStroke(strokes[i], guideColor, strokeWidth * 0.5f, 0.3f)
        }
    }
}

private fun DrawScope.drawGrid(drawScope: DrawScope, color: Color) {
    val gridSize = size.minDimension
    val gridLines = 4
    val lineSpacing = gridSize / gridLines
    
    for (i in 0..gridLines) {
        val offset = i * lineSpacing
        // Vertical lines
        drawScope.drawLine(
            color = color,
            start = Offset(offset, 0f),
            end = Offset(offset, gridSize),
            strokeWidth = 1.dp.toPx(),
            alpha = 0.3f
        )
        // Horizontal lines
        drawScope.drawLine(
            color = color,
            start = Offset(0f, offset),
            end = Offset(gridSize, offset),
            strokeWidth = 1.dp.toPx(),
            alpha = 0.3f
        )
    }
}

private fun DrawScope.drawStroke(
    stroke: Stroke,
    color: Color,
    strokeWidth: Float,
    progress: Float
) {
    // This is a simplified implementation
    // In a real app, you would parse the SVG path data and draw the actual stroke
    val path = Path()
    
    // For demonstration, we'll draw simple strokes based on stroke order
    val canvasSize = size.minDimension
    val centerX = canvasSize / 2
    val centerY = canvasSize / 2
    val strokeLength = canvasSize * 0.3f
    
    // Create a simple path based on stroke order (this is simplified)
    when (stroke.order % 4) {
        1 -> {
            // Horizontal stroke
            path.moveTo(centerX - strokeLength / 2, centerY - strokeLength / 4)
            path.lineTo(centerX + strokeLength / 2, centerY - strokeLength / 4)
        }
        2 -> {
            // Vertical stroke
            path.moveTo(centerX, centerY - strokeLength / 2)
            path.lineTo(centerX, centerY + strokeLength / 2)
        }
        3 -> {
            // Diagonal stroke
            path.moveTo(centerX - strokeLength / 3, centerY - strokeLength / 3)
            path.lineTo(centerX + strokeLength / 3, centerY + strokeLength / 3)
        }
        0 -> {
            // Curved stroke
            path.moveTo(centerX - strokeLength / 2, centerY)
            path.quadraticBezierTo(
                centerX, centerY - strokeLength / 2,
                centerX + strokeLength / 2, centerY
            )
        }
    }
    
    // Draw the path with progress
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(path, false)
    val pathLength = pathMeasure.length
    
    val progressPath = Path()
    pathMeasure.getSegment(0f, pathLength * progress, progressPath, true)
    
    drawPath(
        path = progressPath,
        color = color,
        style = DrawStroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Helper function to get pronunciation (romaji) for a hiragana character
 */
private fun getHiraganaPronunciation(hiragana: String): String {
    return when (hiragana) {
        "あ" -> "a"; "い" -> "i"; "う" -> "u"; "え" -> "e"; "お" -> "o"
        "か" -> "ka"; "き" -> "ki"; "く" -> "ku"; "け" -> "ke"; "こ" -> "ko"
        "さ" -> "sa"; "し" -> "shi"; "す" -> "su"; "せ" -> "se"; "そ" -> "so"
        "た" -> "ta"; "ち" -> "chi"; "つ" -> "tsu"; "て" -> "te"; "と" -> "to"
        "な" -> "na"; "に" -> "ni"; "ぬ" -> "nu"; "ね" -> "ne"; "の" -> "no"
        "は" -> "ha"; "ひ" -> "hi"; "ふ" -> "fu"; "へ" -> "he"; "ほ" -> "ho"
        "ま" -> "ma"; "み" -> "mi"; "む" -> "mu"; "め" -> "me"; "も" -> "mo"
        "や" -> "ya"; "ゆ" -> "yu"; "よ" -> "yo"
        "ら" -> "ra"; "り" -> "ri"; "る" -> "ru"; "れ" -> "re"; "ろ" -> "ro"
        "わ" -> "wa"; "を" -> "wo"; "ん" -> "n"
        // Katakana
        "ア" -> "a"; "イ" -> "i"; "ウ" -> "u"; "エ" -> "e"; "オ" -> "o"
        "カ" -> "ka"; "キ" -> "ki"; "ク" -> "ku"; "ケ" -> "ke"; "コ" -> "ko"
        "サ" -> "sa"; "シ" -> "shi"; "ス" -> "su"; "セ" -> "se"; "ソ" -> "so"
        "タ" -> "ta"; "チ" -> "chi"; "ツ" -> "tsu"; "テ" -> "te"; "ト" -> "to"
        "ナ" -> "na"; "ニ" -> "ni"; "ヌ" -> "nu"; "ネ" -> "ne"; "ノ" -> "no"
        "ハ" -> "ha"; "ヒ" -> "hi"; "フ" -> "fu"; "ヘ" -> "he"; "ホ" -> "ho"
        "マ" -> "ma"; "ミ" -> "mi"; "ム" -> "mu"; "メ" -> "me"; "モ" -> "mo"
        "ヤ" -> "ya"; "ユ" -> "yu"; "ヨ" -> "yo"
        "ラ" -> "ra"; "リ" -> "ri"; "ル" -> "ru"; "レ" -> "re"; "ロ" -> "ro"
        "ワ" -> "wa"; "ヲ" -> "wo"; "ン" -> "n"
        else -> hiragana
    }
}