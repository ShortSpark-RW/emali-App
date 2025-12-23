package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.util.components.auth.EmailOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OrDivider
import com.shortspark.emaliestates.util.components.auth.PasswordOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.SocialAuthButtons
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

private fun validateEmail(email: String): String? =
    when {
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Enter a valid email"
        else -> null
    }

private fun validatePassword(password: String): String? =
    when {
        password.isBlank() -> "Password is required"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

@Composable
fun SigninScreen(
    navController: NavController,
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val loginState by authViewModel.loginState

    LaunchedEffect(loginState) {
        if (loginState is RequestState.Success) {
            navController.navigate(BaseScreen.Home.route) {
                popUpTo(AuthScreen.SignIn.route) { inclusive = true }
            }
        }
    }

    SigninContent(
        loginState = loginState,
        onLogin = { email, password ->
            authViewModel.login(email, password)
        },
        onSignup = {
            navController.navigate(AuthScreen.SignUp.route)
        },
        onForgotPassword = {
            navController.navigate(AuthScreen.ForgotPassword.route)
        }
    )
}

@Composable
@Preview(showBackground = true)
fun SigninContent(
    loginState: RequestState<*> = RequestState.Idle,
    onLogin: (String, String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSignup: () -> Unit = {}
) {
    var state by remember { mutableStateOf(SignInUiState()) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LogoSection(
                title = "Welcome Back",
                subtitle = "Sign in to your account"
            )

            Spacer(modifier = Modifier.height(8.dp))

            /* ---------- API Error ---------- */
            if (loginState is RequestState.Error) {
                println(loginState.message)
                Text(
                    text = loginState.message.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- Email ---------- */
            EmailOutlinedTextField(
                email = state.email,
                onEmailChange = {
                    val emailError = validateEmail(it)
                    state = state.copy(email = it, emailError = emailError)
                },
                isEmailFocused = isEmailFocused,
                onFocusChange = { isEmailFocused = it.isFocused },
                imeAction = ImeAction.Next,
                errorMessage = state.emailError ?: "",
                isError = state.emailError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            /* ---------- Password ---------- */
            PasswordOutlinedTextField(
                value = state.password,
                onValueChange = {
                    val passwordError = validatePassword(it)
                    state = state.copy(password = it, passwordError = passwordError)
                },
                isPasswordFocused = isPasswordFocused,
                onFocusChange = { isPasswordFocused = it.isFocused },
                passwordVisibility = passwordVisibility,
                onVisibilityChange = { passwordVisibility = it },
                imeAction = ImeAction.Done,
                errorMessage = state.passwordError ?: "",
                isError = state.passwordError != null
            )

            Spacer(modifier = Modifier.height(6.dp))

            /* ---------- Remember / Forgot ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = {
                            state = state.copy(rememberMe = it)
                        }
                    )
                    Text("Remember me")
                }

                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* ---------- Submit ---------- */
            AppButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                text = "Sign In",
                modifier = Modifier.fillMaxWidth(),
                textColor = MaterialTheme.colorScheme.onSecondary,
                loading = loginState is RequestState.Loading,
                onClick = {

                    onLogin(state.email, state.password)
                },
            )

            OrDivider()

            SocialAuthButtons()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                buildAnnotatedString {
                    append("Don't have an account?")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(" Sign up")
                    }
                },
                modifier = Modifier.clickable { onSignup() }
            )
        }
    }
}
