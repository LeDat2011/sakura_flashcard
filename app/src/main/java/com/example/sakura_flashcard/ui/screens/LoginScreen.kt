package com.example.sakura_flashcard.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sakura_flashcard.data.auth.GoogleAuthManager
import com.example.sakura_flashcard.ui.theme.AppColors
import com.example.sakura_flashcard.ui.theme.AppTypography
import kotlinx.coroutines.launch

// Helper function to find Activity from Context
private fun Context.findActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Lấy FragmentActivity từ context - bây giờ MainActivity là AppCompatActivity (extends FragmentActivity)
    val activity: FragmentActivity? = context as? FragmentActivity
    android.util.Log.d("BiometricLogin", "LoginScreen: activity=$activity, context=${context::class.java.name}")
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

                // Login Button Row với nút vân tay nhỏ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Login Button (chiếm phần lớn)
                    Button(
                        onClick = viewModel::login,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
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

                    // Nút vân tay - CHỈ HIỆN KHI ĐÃ BẬT TRONG PROFILE
                    if (uiState.canUseBiometric) {
                        FilledIconButton(
                            onClick = { 
                                android.util.Log.d("BiometricLogin", "Fingerprint button clicked")
                                if (activity != null) {
                                    viewModel.biometricLogin(activity)
                                } else {
                                    android.util.Log.e("BiometricLogin", "Activity is NULL!")
                                }
                            },
                            modifier = Modifier.size(52.dp),
                            enabled = !uiState.isLoading,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = AppColors.PrimaryLight
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Fingerprint,
                                contentDescription = "Đăng nhập vân tay",
                                modifier = Modifier.size(28.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                // Hint text khi thiết bị hỗ trợ biometric nhưng chưa bật
                if (uiState.isBiometricAvailable && !uiState.canUseBiometric) {
                    Text(
                        text = "💡 Bật đăng nhập vân tay trong Hồ sơ sau khi đăng nhập",
                        style = AppTypography.BodySmall,
                        color = AppColors.TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = AppColors.SurfaceBorder)

                // OTP Login Option
                OutlinedButton(
                    onClick = { viewModel.sendOTP() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AppColors.SurfaceBorder)
                ) {
                    Text(text = "📧 Đăng nhập bằng mã OTP (Email)", style = AppTypography.TitleMedium, color = AppColors.TextPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password Link
                TextButton(
                    onClick = { viewModel.forgotPassword() },
                    enabled = !uiState.isLoading
                ) {
                    Text(text = "🔐 Quên mật khẩu?", style = AppTypography.BodyMedium, color = AppColors.PrimaryLight)
                }
            }
        }

        // OTP Dialog
        if (uiState.isOtpSent) {
            var otpCode by remember { mutableStateOf("") }
            Dialog(onDismissRequest = { }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Nhập mã OTP", style = AppTypography.HeadlineSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Mã đã được gửi đến email của bạn", style = AppTypography.BodyMedium, color = AppColors.TextSecondary, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6) otpCode = it },
                            label = { Text("Mã OTP") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.verifyOTP(otpCode) },
                            enabled = otpCode.length == 6 && !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Xác nhận")
                        }
                    }
                }
            }
        }

        // Forgot Password Dialog
        val isForgotPasswordSent by viewModel.isForgotPasswordSent.collectAsStateWithLifecycle()
        val isResetPasswordSuccess by viewModel.isResetPasswordSuccess.collectAsStateWithLifecycle()
        
        if (isForgotPasswordSent) {
            var resetToken by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }
            var showPassword by remember { mutableStateOf(false) }
            
            Dialog(onDismissRequest = { viewModel.clearForgotPasswordState() }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔐 Đặt lại mật khẩu", style = AppTypography.HeadlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Mã xác nhận đã được gửi đến email của bạn",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = resetToken,
                            onValueChange = { if (it.length <= 6) resetToken = it },
                            label = { Text("Mã xác nhận (6 số)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Mật khẩu mới") },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Xác nhận mật khẩu") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword,
                            supportingText = if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) {
                                { Text("Mật khẩu không khớp", color = MaterialTheme.colorScheme.error) }
                            } else null
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { viewModel.resetPassword(resetToken, newPassword) },
                            enabled = resetToken.length == 6 && 
                                     newPassword.length >= 8 && 
                                     newPassword == confirmPassword && 
                                     !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Đặt lại mật khẩu")
                        }
                        
                        TextButton(onClick = { viewModel.clearForgotPasswordState() }) {
                            Text("Hủy", color = AppColors.TextSecondary)
                        }
                    }
                }
            }
        }

        // Success Dialog after Reset Password
        if (isResetPasswordSuccess) {
            Dialog(onDismissRequest = { viewModel.clearForgotPasswordState() }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceLight),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "✅", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Thành công!", style = AppTypography.HeadlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Mật khẩu đã được đặt lại. Vui lòng đăng nhập với mật khẩu mới.",
                            style = AppTypography.BodyMedium,
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.clearForgotPasswordState() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryLight)
                        ) {
                            Text("Đăng nhập")
                        }
                    }
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
