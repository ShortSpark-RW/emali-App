package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.PasswordOutlinedTextField
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    ChangePasswordContent(navController)
}


@Composable
@Preview(showBackground = true)
fun ChangePasswordContent(
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val resetPasswordState by authViewModel.resetPasswordState.collectAsState()
    val verificationEmail by authViewModel.verificationEmail.collectAsState()

    LaunchedEffect(resetPasswordState) {
        if (resetPasswordState is RequestState.Success) {
            // Password reset, clear state and navigate to sign-in
            authViewModel.clearVerificationEmail()
            navController.navigate(AuthScreen.SignIn.route) {
                popUpTo(AuthScreen.ChangePassword.route) { inclusive = true }
            }
            authViewModel.resetResetPasswordState()
        }
    }

    val resetPasswordError = (resetPasswordState as? RequestState.Error)?.message

    val email = verificationEmail
    if (email == null) {
        androidx.compose.material3.Text(
            text = "No email address found. Please go back and try again.",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LogoSection(
                    title = "Reset Password",
                    subtitle = "Please enter your new password"
                )

                if (resetPasswordError != null) {
                    androidx.compose.material3.Text(
                        text = resetPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                PasswordOutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = validatePassword(it)
                        // Clear confirm error if passwords match
                        if (confirmPassword.isNotBlank() && it == confirmPassword) {
                            confirmPasswordError = null
                        }
                    },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    passwordVisibility = passwordVisibility,
                    onVisibilityChange = { passwordVisibility = it },
                    isPasswordFocused = isPasswordFocused,
                    imeAction = ImeAction.Next,
                    onFocusChange = { isPasswordFocused = it.isFocused },
                    errorMessage = passwordError ?: "",
                    isError = passwordError != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordOutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = if (it != password) {
                            "Passwords do not match"
                        } else {
                            null
                        }
                    },
                    label = "Confirm Password",
                    placeholder = "Confirm your password",
                    modifier = Modifier.fillMaxWidth(),
                    passwordVisibility = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = it },
                    isPasswordFocused = isConfirmPasswordFocused,
                    imeAction = ImeAction.Done,
                    onFocusChange = { isConfirmPasswordFocused = it.isFocused },
                    errorMessage = confirmPasswordError ?: "",
                    isError = confirmPasswordError != null
                )

                Spacer(modifier = Modifier.height(12.dp))


                AppButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    text = "Reset Password",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    loading = resetPasswordState is RequestState.Loading,
                    onClick = {
                        val passErr = validatePassword(password)
                        val confirmErr = if (password != confirmPassword) {
                            "Passwords do not match"
                        } else {
                            null
                        }

                        passwordError = passErr
                        confirmPasswordError = confirmErr

                        if (passErr != null || confirmErr != null) {
                            return@AppButton
                        }

                        authViewModel.resetPassword(email, password)
                    }
                )
            }

        }
    }
}

private fun validatePassword(password: String): String? {
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    return when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters long"
        !passwordRegex.matches(password) -> "Password must contain an uppercase letter, a number, and a special character"
        else -> null
    }
}
