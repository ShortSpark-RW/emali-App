package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.util.components.auth.EmailOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    ForgotPasswordContent(navController)
}


@Composable
@Preview(showBackground = true)
fun ForgotPasswordContent(
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val forgotPasswordState by authViewModel.forgotPasswordState.collectAsState()

    // State variables must be declared before LaunchedEffect that uses them
    var email by remember { mutableStateOf("") }
    var isEmailFocused by remember { mutableStateOf(false) }
    var emailError by mutableStateOf<String?>(null)

    LaunchedEffect(forgotPasswordState) {
        if (forgotPasswordState is RequestState.Success) {
            // Store email for OTP verification screen
            authViewModel.setVerificationEmail(email)
            navController.navigate(AuthScreen.VerifyOtp.route)
            // Reset state to avoid re-trigger when coming back
            authViewModel.resetForgotPasswordState()
        }
    }

    val forgotPasswordError = (forgotPasswordState as? RequestState.Error)?.message

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
                content = {
                    Spacer(modifier = Modifier.height(16.dp))

                    LogoSection(
                        title = "Forgot Password",
                        subtitle = "Please enter your email address to receive a verification code"
                    )

                    if (forgotPasswordError != null) {
                        androidx.compose.material3.Text(
                            text = forgotPasswordError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                EmailOutlinedTextField(
                    email = email,
                    onEmailChange = {
                        email = it
                        emailError = validateEmail(it)
                    },
                    isEmailFocused = isEmailFocused,
                    onFocusChange = { isEmailFocused = it.isFocused },
                    imeAction = ImeAction.Done,
                    errorMessage = emailError ?: "",
                    isError = emailError != null
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    buildAnnotatedString {
                        append("Didn't receive a code?")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Normal,
                            )
                        ) {
                            append(" Try another way")
                        }
                    },
                    modifier = Modifier.clickable {
                        // Could navigate to support screen or retry
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    text = "Send Code",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    loading = forgotPasswordState is RequestState.Loading,
                    onClick = {
                        val emailErr = validateEmail(email)
                        if (emailErr != null) {
                            // Show error (could also use a state)
                            return@AppButton
                        }
                        authViewModel.forgotPassword(email)
                    }
                )
            }
        }
    }
}

private fun validateEmail(email: String): String? {
    val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return when {
        email.isBlank() -> "Email is required"
        !emailRegex.matches(email) -> "Enter a valid email address"
        else -> null
    }
}
