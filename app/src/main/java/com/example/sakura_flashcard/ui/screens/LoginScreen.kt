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
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
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
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) onLoginSuccess()
    }

    val gradientColors = listOf(Color(0xFFfdf2f8), Color(0xFFfce7f3), Color(0xFFfff1f2))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // App Logo
        Surface(
            color = Color(0xFFfbcfe8),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.size(120.dp),
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "\uD83C\uDF38", fontSize = 56.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Sakura Flashcard",
            style = AppTypography.HeadlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFbe185d)
        )

        Text(
            text = "Học tiếng Nhật với thẻ flashcard tương tác",
            style = AppTypography.BodyMedium,
            color = AppColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Login Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
            border = BorderStroke(1.dp, AppColors.SurfaceBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Chào Mừng Trở Lại \uD83D\uDC4B",
                    style = AppTypography.HeadlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )

                // Email Field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Rounded.Email, "Email", tint = AppColors.PrimaryLight) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    isError = uiState.emailError != null,
                    supportingText = uiState.emailError?.let { { Text(it, color = AppColors.DangerLight) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryLight,
                        unfocusedBorderColor = AppColors.SurfaceBorder
                    )
                )

                // Password Field
                var passwordVisible by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::updatePassword,
                    label = { Text("Mật khẩu") },
                    leadingIcon = { Icon(Icons.Rounded.Lock, "Mật khẩu", tint = AppColors.PrimaryLight) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                tint = AppColors.TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); viewModel.login() }),
                    isError = uiState.passwordError != null,
                    supportingText = uiState.passwordError?.let { { Text(it, color = AppColors.DangerLight) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.PrimaryLight,
                        unfocusedBorderColor = AppColors.SurfaceBorder
                    )
                )

                // Error Message
                AnimatedVisibility(visible = uiState.errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                    uiState.errorMessage?.let { error ->
                        Surface(color = Color(0xFFfee2e2), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(text = error, color = AppColors.DangerLight, style = AppTypography.BodyMedium, modifier = Modifier.padding(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login Button
                Button(
                    onClick = viewModel::login,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !uiState.isLoading && uiState.isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (uiState.isLoading) "Đang đăng nhập..." else "Đăng nhập",
                        style = AppTypography.TitleMedium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Link
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Chưa có tài khoản? ", style = AppTypography.BodyMedium, color = AppColors.TextSecondary)
            TextButton(onClick = onNavigateToRegister, enabled = !uiState.isLoading) {
                Text(text = "Đăng ký", style = AppTypography.TitleMedium, fontWeight = FontWeight.Bold, color = AppColors.PrimaryLight)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}