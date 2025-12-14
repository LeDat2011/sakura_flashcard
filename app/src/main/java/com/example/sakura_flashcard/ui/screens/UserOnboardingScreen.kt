package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sakura_flashcard.data.model.DailyStudyGoal
import com.example.sakura_flashcard.data.model.JLPTLevel
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun UserOnboardingScreen(
    onComplete: (
        displayName: String,
        age: Int?,
        currentLevel: JLPTLevel,
        targetLevel: JLPTLevel,
        dailyGoalMinutes: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var currentLevel by remember { mutableStateOf(JLPTLevel.N5) }
    var targetLevel by remember { mutableStateOf(JLPTLevel.N3) }
    var dailyGoal by remember { mutableStateOf(DailyStudyGoal.FIFTEEN_MINUTES) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    
    val isFormValid = displayName.isNotBlank() && displayName.length >= 2

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Header
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = AppColors.PrimaryLight
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Chào mừng bạn!",
            style = AppTypography.HeadlineLarge,
            color = AppColors.TextPrimary
        )
        
        Text(
            text = "Hãy cho chúng tôi biết thêm về bạn",
            style = AppTypography.BodyLarge,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Name Input
        OnboardingSection(title = "Tên của bạn") {
            OutlinedTextField(
                value = displayName,
                onValueChange = { 
                    displayName = it
                    nameError = if (it.length < 2 && it.isNotEmpty()) "Tên phải có ít nhất 2 ký tự" else null
                },
                placeholder = { Text("Nhập tên của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.PrimaryLight,
                    unfocusedBorderColor = AppColors.SurfaceBorder
                ),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = AppColors.DangerLight) } },
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Age Input (Optional)
        OnboardingSection(title = "Tuổi (không bắt buộc)") {
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) age = it },
                placeholder = { Text("Nhập tuổi của bạn") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.PrimaryLight,
                    unfocusedBorderColor = AppColors.SurfaceBorder
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Current Level
        OnboardingSection(title = "Trình độ hiện tại của bạn") {
            JLPTLevelSelector(
                selectedLevel = currentLevel,
                onLevelSelected = { currentLevel = it }
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Target Level
        OnboardingSection(title = "Trình độ mong muốn") {
            JLPTLevelSelector(
                selectedLevel = targetLevel,
                onLevelSelected = { targetLevel = it }
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Daily Study Goal
        OnboardingSection(title = "Thời gian học mỗi ngày") {
            DailyGoalSelector(
                selectedGoal = dailyGoal,
                onGoalSelected = { dailyGoal = it }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Submit Button
        Button(
            onClick = {
                onComplete(
                    displayName.trim(),
                    age.toIntOrNull(),
                    currentLevel,
                    targetLevel,
                    dailyGoal.minutes
                )
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryLight,
                disabledContainerColor = AppColors.TextDisabled
            )
        ) {
            Text(
                text = "Bắt đầu học",
                style = AppTypography.TitleMedium,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OnboardingSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun JLPTLevelSelector(
    selectedLevel: JLPTLevel,
    onLevelSelected: (JLPTLevel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JLPTLevel.values().forEach { level ->
            val isSelected = level == selectedLevel
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLevelSelected(level) },
                color = if (isSelected) AppColors.PrimaryLight else AppColors.SurfaceLight,
                border = BorderStroke(
                    1.dp, 
                    if (isSelected) AppColors.PrimaryLight else AppColors.SurfaceBorder
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = level.displayName,
                        style = AppTypography.LabelLarge,
                        color = if (isSelected) Color.White else AppColors.TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyGoalSelector(
    selectedGoal: DailyStudyGoal,
    onGoalSelected: (DailyStudyGoal) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row: 5, 10, 15, 20 minutes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                DailyStudyGoal.FIVE_MINUTES,
                DailyStudyGoal.TEN_MINUTES,
                DailyStudyGoal.FIFTEEN_MINUTES,
                DailyStudyGoal.TWENTY_MINUTES
            ).forEach { goal ->
                DailyGoalChip(
                    goal = goal,
                    isSelected = goal == selectedGoal,
                    onClick = { onGoalSelected(goal) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Second row: 30, 45, 60 minutes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                DailyStudyGoal.THIRTY_MINUTES,
                DailyStudyGoal.FORTY_FIVE_MINUTES,
                DailyStudyGoal.ONE_HOUR
            ).forEach { goal ->
                DailyGoalChip(
                    goal = goal,
                    isSelected = goal == selectedGoal,
                    onClick = { onGoalSelected(goal) },
                    modifier = Modifier.weight(1f)
                )
            }
            // Spacer to balance the row
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun DailyGoalChip(
    goal: DailyStudyGoal,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) AppColors.PrimaryLightest else AppColors.SurfaceLight,
        border = BorderStroke(
            1.dp,
            if (isSelected) AppColors.PrimaryLight else AppColors.SurfaceBorder
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AppColors.PrimaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = goal.displayName,
                    style = AppTypography.LabelLarge,
                    color = if (isSelected) AppColors.PrimaryLight else AppColors.TextSecondary
                )
            }
        }
    }
}
