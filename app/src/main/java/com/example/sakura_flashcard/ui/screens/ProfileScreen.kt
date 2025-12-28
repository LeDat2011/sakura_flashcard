package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit = {},
    onNavigateToCustomDecks: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val customDecks by viewModel.customDecks.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.SurfaceMedium)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ProfileHeader(userProfile = uiState.userProfile, onEditClick = onNavigateToEdit) }
        item { LearningProgressCard(userStats = uiState.userStats) }
        item { CustomDecksCard(decks = customDecks, onManageDecksClick = onNavigateToCustomDecks, onCreateDeck = { viewModel.createCustomDeck(it) }) }
        item { 
            ProfileActionsCard(
                onLogout = { viewModel.logout(); onLogout() },
                isBiometricEnabled = isBiometricEnabled,
                userEmail = uiState.userProfile?.email ?: "",
                onToggleBiometric = { enabled, email, password ->
                    viewModel.toggleBiometric(enabled, email, password)
                }
            )
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AppColors.PrimaryLight, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Đang tải...", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun ProfileHeader(userProfile: com.example.sakura_flashcard.data.api.UserProfile?, onEditClick: () -> Unit) {
    val gradientColors = listOf(Color(0xFFfdf2f8), Color(0xFFfce7f3), Color(0xFFfff1f2))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(colors = gradientColors), shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = AppColors.PrimaryLight, shape = CircleShape, modifier = Modifier.size(80.dp), shadowElevation = 4.dp) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(userProfile?.username?.firstOrNull()?.uppercase() ?: "\uD83D\uDC64", style = AppTypography.HeadlineLarge, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(userProfile?.username ?: "User", style = AppTypography.HeadlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFbe185d))
                Text(userProfile?.email ?: "", style = AppTypography.BodySmall, color = AppColors.TextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Edit, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Chỉnh sửa hồ sơ", style = AppTypography.TitleMedium, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LearningProgressCard(userStats: com.example.sakura_flashcard.data.api.UserStatsDto?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        border = BorderStroke(1.dp, AppColors.SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("\uD83D\uDCCA", fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text("Tiến trình học tập", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            }

            if (userStats != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProgressStatItem(Icons.Rounded.Style, userStats.vocabulary.mastered.toString(), "Từ vựng", Color(0xFFdbeafe), Color(0xFF3b82f6))
                    ProgressStatItem(Icons.Rounded.Quiz, userStats.quiz.totalAttempts.toString(), "Bài kiểm tra", Color(0xFFd1fae5), Color(0xFF10b981))
                    ProgressStatItem(Icons.Rounded.LocalFireDepartment, (userStats.user?.currentStreak ?: 0).toString(), "Chuỗi ngày", Color(0xFFfed7aa), Color(0xFFf97316))
                }
                
                Spacer(Modifier.height(16.dp))
                Surface(color = Color(0xFFf0fdf4), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFF16a34a), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("XP: ${userStats.user?.totalXP ?: 0}", style = AppTypography.BodyMedium, fontWeight = FontWeight.Medium, color = Color(0xFF16a34a))
                    }
                }
            } else {
                Surface(color = Color(0xFFf8fafc), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Chưa có dữ liệu", style = AppTypography.BodyMedium, color = AppColors.TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
            }
        }
    }
}

@Composable
private fun JLPTProgressItem(level: com.example.sakura_flashcard.data.model.JLPTLevel, progress: Float) {
    val levelName = level.name
    val levelColor = when (levelName) { "N5" -> Color(0xFF22c55e); "N4" -> Color(0xFF84cc16); "N3" -> Color(0xFFeab308); "N2" -> Color(0xFFf97316); "N1" -> Color(0xFFef4444); else -> AppColors.PrimaryLight }
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = levelColor, shape = RoundedCornerShape(8.dp)) {
            Text("JLPT $levelName", style = AppTypography.LabelSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Spacer(Modifier.width(12.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)), color = levelColor, trackColor = levelColor.copy(alpha = 0.2f))
        Spacer(Modifier.width(12.dp))
        Text("${(progress * 100).toInt()}%", style = AppTypography.LabelSmall, fontWeight = FontWeight.Bold, color = levelColor, modifier = Modifier.width(40.dp))
    }
}

@Composable
private fun ProgressStatItem(icon: ImageVector, value: String, label: String, bgColor: Color, iconColor: Color) {
    Column(Modifier.background(bgColor, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
        Text(label, style = AppTypography.LabelSmall, color = AppColors.TextSecondary)
    }
}

@Composable
private fun CustomDecksCard(decks: List<CustomDeck>, onManageDecksClick: () -> Unit, onCreateDeck: (String) -> Unit) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        border = BorderStroke(1.dp, AppColors.SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDCDD", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Bộ thẻ tùy chỉnh", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
                }
                Surface(color = AppColors.PrimaryLightest, shape = RoundedCornerShape(8.dp), onClick = onManageDecksClick) {
                    Text("Quản lý", style = AppTypography.LabelSmall, color = AppColors.PrimaryLight, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            if (decks.isEmpty()) {
                Surface(color = Color(0xFFf8fafc), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Chưa tạo bộ thẻ nào", style = AppTypography.BodyMedium, color = AppColors.TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(20.dp))
                }
            } else {
                decks.take(3).forEach { CustomDeckItem(it) }
                if (decks.size > 3) Text("Va ${decks.size - 3} bo the khac...", style = AppTypography.BodySmall, color = AppColors.TextSecondary, modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { showCreateDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.SurfaceLight),
                border = BorderStroke(1.dp, AppColors.PrimaryLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Add, null, tint = AppColors.PrimaryLight, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Tạo bộ thẻ mới", style = AppTypography.TitleMedium, color = AppColors.PrimaryLight)
            }
        }
    }
    if (showCreateDialog) CreateDeckDialog(onDismiss = { showCreateDialog = false }, onConfirm = { onCreateDeck(it); showCreateDialog = false })
}

@Composable
private fun CustomDeckItem(deck: CustomDeck) {
    Surface(color = Color(0xFFf0fdf4), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFF86efac), shape = RoundedCornerShape(8.dp)) {
                Icon(Icons.Rounded.Folder, null, tint = Color(0xFF16a34a), modifier = Modifier.padding(8.dp).size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(deck.name, style = AppTypography.BodyMedium, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                Text("${deck.flashcardCount} the", style = AppTypography.LabelSmall, color = Color(0xFF16a34a))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = AppColors.TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun CreateDeckDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var deckName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = AppColors.SurfaceLight, shape = RoundedCornerShape(20.dp),
        title = { Text("Tạo bộ thẻ mới", style = AppTypography.TitleLarge, color = AppColors.TextPrimary) },
        text = { OutlinedTextField(value = deckName, onValueChange = { deckName = it }, label = { Text("Tên bộ thẻ") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = { onConfirm(deckName) }, enabled = deckName.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight), shape = RoundedCornerShape(12.dp)) { Text("Tạo", color = Color.White) } },
        dismissButton = { TextButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) { Text("Hủy", color = AppColors.TextSecondary) } }
    )
}

@Composable
private fun ProfileActionsCard(
    onLogout: () -> Unit,
    isBiometricEnabled: Boolean = false,
    userEmail: String = "",
    onToggleBiometric: (Boolean, String, String) -> Unit = { _, _, _ -> }
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var biometricPassword by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        border = BorderStroke(1.dp, AppColors.SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2699\uFE0F", fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text("Tùy chọn tài khoản", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            }
            Spacer(Modifier.height(16.dp))
            
            // Biometric toggle
            Surface(
                color = Color(0xFFfdf4ff),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            Icons.Rounded.Fingerprint,
                            contentDescription = null,
                            tint = AppColors.PrimaryLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Đăng nhập vân tay",
                                style = AppTypography.BodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.TextPrimary
                            )
                            Text(
                                text = if (isBiometricEnabled) "Đã bật" else "Chưa bật",
                                style = AppTypography.LabelSmall,
                                color = if (isBiometricEnabled) Color(0xFF16a34a) else AppColors.TextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                showBiometricDialog = true
                            } else {
                                onToggleBiometric(false, "", "")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppColors.PrimaryLight,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = AppColors.SurfaceBorder
                        )
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFfee2e2)),
                border = BorderStroke(1.dp, AppColors.DangerLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.ExitToApp, null, tint = AppColors.DangerLight, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất", style = AppTypography.TitleMedium, color = AppColors.DangerLight)
            }
        }
    }
    
    if (showLogoutDialog) LogoutConfirmationDialog(onDismiss = { showLogoutDialog = false }, onConfirm = { showLogoutDialog = false; onLogout() })
    
    // Biometric enable dialog
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false; biometricPassword = "" },
            containerColor = AppColors.SurfaceLight,
            shape = RoundedCornerShape(20.dp),
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Fingerprint, null, tint = AppColors.PrimaryLight)
                    Spacer(Modifier.width(8.dp))
                    Text("Bật đăng nhập vân tay", style = AppTypography.TitleLarge, color = AppColors.TextPrimary)
                }
            },
            text = {
                Column {
                    Text(
                        "Nhập mật khẩu để xác nhận bật đăng nhập bằng vân tay",
                        style = AppTypography.BodyMedium,
                        color = AppColors.TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = biometricPassword,
                        onValueChange = { biometricPassword = it },
                        label = { Text("Mật khẩu") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = AppColors.PrimaryLight) },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleBiometric(true, userEmail, biometricPassword)
                        showBiometricDialog = false
                        biometricPassword = ""
                    },
                    enabled = biometricPassword.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Bật", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBiometricDialog = false; biometricPassword = "" },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hủy", color = AppColors.TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, containerColor = AppColors.SurfaceLight, shape = RoundedCornerShape(20.dp),
        title = { Text("Xác nhận đăng xuất", style = AppTypography.TitleLarge, color = AppColors.TextPrimary) },
        text = { Text("Bạn có chắc chắn muốn đăng xuất?", style = AppTypography.BodyMedium, color = AppColors.TextSecondary) },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = AppColors.DangerLight), shape = RoundedCornerShape(12.dp)) { Text("Đăng xuất", color = Color.White) } },
        dismissButton = { TextButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) { Text("Hủy", color = AppColors.TextSecondary) } }
    )
}
