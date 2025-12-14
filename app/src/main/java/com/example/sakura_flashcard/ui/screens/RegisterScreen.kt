package com.example.sakura_flashcard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) onRegistrationSuccess()
    }

    val gradientColors = listOf(Color(0xFFf0f9ff), Color(0xFFe0f2fe), Color(0xFFf0fdfa))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Tạo tài khoản", style = AppTypography.TitleLarge, color = AppColors.TextPrimary) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Rounded.ArrowBack, "Quay lại", tint = AppColors.PrimaryLight)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Text
            Text(text = "\uD83C\uDF38", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tham gia Sakura Flashcard",
                style = AppTypography.HeadlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
            Text(
                text = "Bắt đầu hành trình học tiếng Nhật của bạn hôm nay",
                style = AppTypography.BodyMedium,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Registration Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                border = BorderStroke(1.dp, AppColors.SurfaceBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Username Field
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = viewModel::updateUsername,
                        label = { Text("Tên người dùng") },
                        leadingIcon = { Icon(Icons.Rounded.Person, null, tint = AppColors.PrimaryLight) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        isError = uiState.usernameError != null,
                        supportingText = uiState.usernameError?.let { { Text(it, color = AppColors.DangerLight) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.PrimaryLight, unfocusedBorderColor = AppColors.SurfaceBorder)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail,
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Rounded.Email, null, tint = AppColors.PrimaryLight) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        isError = uiState.emailError != null,
                        supportingText = uiState.emailError?.let { { Text(it, color = AppColors.DangerLight) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.PrimaryLight, unfocusedBorderColor = AppColors.SurfaceBorder)
                    )

                    // Password Field
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::updatePassword,
                        label = { Text("Mật khẩu") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = AppColors.PrimaryLight) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, null, tint = AppColors.TextSecondary)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        isError = uiState.passwordError != null,
                        supportingText = uiState.passwordError?.let { { Text(it, color = AppColors.DangerLight) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.PrimaryLight, unfocusedBorderColor = AppColors.SurfaceBorder)
                    )

                    // Confirm Password Field
                    var confirmPasswordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::updateConfirmPassword,
                        label = { Text("Xác nhận mật khẩu") },
                        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = AppColors.PrimaryLight) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(if (confirmPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, null, tint = AppColors.TextSecondary)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.register() }),
                        isError = uiState.confirmPasswordError != null,
                        supportingText = uiState.confirmPasswordError?.let { { Text(it, color = AppColors.DangerLight) } },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.PrimaryLight, unfocusedBorderColor = AppColors.SurfaceBorder)
                    )

                    // Password Requirements
                    if (uiState.password.isNotEmpty()) {
                        Surface(color = Color(0xFFf8fafc), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Yêu cầu mật khẩu:", style = AppTypography.LabelSmall, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                PasswordRequirement("Ít nhất 8 ký tự", uiState.password.length >= 8)
                                PasswordRequirement("Một chữ hoa", uiState.password.any { it.isUpperCase() })
                                PasswordRequirement("Một chữ thường", uiState.password.any { it.isLowerCase() })
                                PasswordRequirement("Một chữ số", uiState.password.any { it.isDigit() })
                            }
                        }
                    }

                    // Error Message
                    AnimatedVisibility(visible = uiState.errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                        uiState.errorMessage?.let { error ->
                            Surface(color = Color(0xFFfee2e2), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(text = error, color = AppColors.DangerLight, style = AppTypography.BodyMedium, modifier = Modifier.padding(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Register Button
                    Button(
                        onClick = viewModel::register,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = !uiState.isLoading && uiState.isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (uiState.isLoading) "Đang tạo tài khoản..." else "Tạo tài khoản", style = AppTypography.TitleMedium, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản? ", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
                TextButton(onClick = onNavigateBack, enabled = !uiState.isLoading) {
                    Text("Đăng nhập", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.PrimaryLight)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PasswordRequirement(requirement: String, isMet: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (isMet) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            null,
            tint = if (isMet) AppColors.SuccessLight else AppColors.TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(requirement, style = AppTypography.BodySmall, color = if (isMet) AppColors.SuccessLight else AppColors.TextSecondary)
    }
}