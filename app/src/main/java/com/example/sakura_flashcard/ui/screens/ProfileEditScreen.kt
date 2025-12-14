package com.example.sakura_flashcard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val editState by viewModel.editState.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BackgroundPrimary)
    ) {
        // Header
        ProfileEditTopBar(onNavigateBack = onNavigateBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileEditHeader(
                    currentAvatar = uiState.userProfile?.profile?.avatar,
                    onAvatarChange = { viewModel.updateAvatar(it) }
                )
            }

            item {
                ProfileEditForm(
                    username = editState.username,
                    email = editState.email,
                    onUsernameChange = { viewModel.updateUsername(it) },
                    onEmailChange = { viewModel.updateEmail(it) }
                )
            }

            item {
                PasswordChangeCard(
                    onChangePasswordClick = { showPasswordDialog = true }
                )
            }

            item {
                SaveProfileButton(
                    isSaving = editState.isSaving,
                    onSave = { viewModel.saveProfile() },
                    onCancel = onNavigateBack
                )
            }
        }
    }

    if (showPasswordDialog) {
        PasswordChangeDialog(
            isChanging = editState.isChangingPassword,
            onDismiss = { showPasswordDialog = false },
            onConfirm = { current, new ->
                viewModel.changePassword(current, new)
                showPasswordDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileEditTopBar(onNavigateBack: () -> Unit) {
    Surface(
        color = AppColors.SurfaceLight,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = AppColors.TextPrimary
                )
            }
            Text(
                text = "Chỉnh sửa hồ sơ",
                style = AppTypography.TitleLarge,
                color = AppColors.TextPrimary
            )
        }
    }
}

@Composable
private fun ProfileEditHeader(
    currentAvatar: String?,
    onAvatarChange: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFfdf4ff), Color(0xFFfae8ff))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(AppColors.PrimaryLightest)
                        .border(3.dp, AppColors.PrimaryLight, CircleShape)
                        .clickable { /* Change avatar */ },
                    contentAlignment = Alignment.Center
                ) {
                    if (currentAvatar != null) {
                        Text(
                            text = currentAvatar,
                            fontSize = 48.sp
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = AppColors.PrimaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Change avatar button
                FilledTonalButton(
                    onClick = { onAvatarChange("🌸") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppColors.SurfaceLight,
                        contentColor = AppColors.PrimaryLight
                    )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đổi ảnh đại diện", style = AppTypography.LabelSmall)
                }
            }
        }
    }
}

@Composable
private fun ProfileEditForm(
    username: String,
    email: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thông tin cá nhân",
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Tên người dùng") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.PrimaryLight
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.PrimaryLight,
                    unfocusedBorderColor = AppColors.SurfaceBorder
                )
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = AppColors.PrimaryLight
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.PrimaryLight,
                    unfocusedBorderColor = AppColors.SurfaceBorder
                )
            )
        }
    }
}

@Composable
private fun PasswordChangeCard(
    onChangePasswordClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Bảo mật",
                style = AppTypography.TitleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onChangePasswordClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AppColors.WarningLight
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(AppColors.WarningLight, AppColors.WarningLight)
                    )
                )
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đổi mật khẩu")
            }
        }
    }
}

@Composable
private fun SaveProfileButton(
    isSaving: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AppColors.TextSecondary
            )
        ) {
            Text("Hủy")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.PrimaryLight
            )
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lưu thay đổi")
            }
        }
    }
}

@Composable
private fun PasswordChangeDialog(
    isChanging: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = AppColors.SurfaceLight,
        title = {
            Text(
                "Đổi mật khẩu",
                style = AppTypography.TitleLarge,
                color = AppColors.TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Mật khẩu hiện tại") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(currentPassword, newPassword) },
                enabled = !isChanging && newPassword == confirmPassword && newPassword.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = AppColors.TextSecondary)
            }
        }
    )
}