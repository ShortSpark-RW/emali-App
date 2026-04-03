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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.domain.auth.validation.ValidationRules
import com.shortspark.emaliestates.util.components.auth.EmailOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OrDivider
import com.shortspark.emaliestates.util.components.auth.PasswordOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.SocialAuthButtons
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SignupScreen(
    navController: NavController,
) {
    SignupContent(navController)
}


@Composable
@Preview(showBackground = true)
fun SignupContent(
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val authViewModel = koinViewModel<AuthViewModel>()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }

    var emailError by mutableStateOf<String?>(null)
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

            Spacer(modifier = Modifier.height(16.dp))

            LogoSection(
                subtitle = "Create a new account"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email field
            EmailOutlinedTextField(
                email = email,
                onEmailChange = {
                    email = it
                    emailError = ValidationRules.validateEmail(it)
                },
                isEmailFocused = isEmailFocused,
                onFocusChange = { isEmailFocused = it.isFocused },
                imeAction = ImeAction.Next,
                errorMessage = emailError ?: "",
                isError = emailError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            PasswordOutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ValidationRules.validatePassword(it)
                    // Clear confirm error if passwords now match
                    if (confirmPassword.isNotBlank() && it == confirmPassword) {
                        confirmPasswordError = null
                    }
                },
                isPasswordFocused = isPasswordFocused,
                onFocusChange = { isPasswordFocused = it.isFocused },
                passwordVisibility = passwordVisibility,
                onVisibilityChange = { passwordVisibility = it },
                imeAction = ImeAction.Next,
                errorMessage = passwordError ?: "",
                isError = passwordError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password field
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
                isPasswordFocused = isConfirmPasswordFocused,
                onFocusChange = { isConfirmPasswordFocused = it.isFocused },
                passwordVisibility = confirmPasswordVisibility,
                onVisibilityChange = { confirmPasswordVisibility = it },
                imeAction = ImeAction.Done,
                errorMessage = confirmPasswordError ?: "",
                isError = confirmPasswordError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.secondary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            checkmarkColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                    Text(
                        text = "Remember me",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable {
                        navController.navigate(AuthScreen.ForgotPassword.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            AppButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                text = "Next",
                textColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                onClick = {
                    // Validate all fields using centralized validation
                    val validationResults = ValidationRules.validateSignUp(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword
                    )

                    val emailErr = validationResults["email"]
                    val passErr = validationResults["password"]
                    val confirmErr = validationResults["confirmPassword"]

                    if (emailErr != null || passErr != null || confirmErr != null) {
                        emailError = emailErr
                        passwordError = passErr
                        confirmPasswordError = confirmErr
                        return@AppButton
                    }

                    // Store credentials in ViewModel for next screen
                    authViewModel.setSignupCredentials(email, password)

                    // Navigate to next step
                    navController.navigate(AuthScreen.SignUp2.route)
                },
            )

            OrDivider()

            SocialAuthButtons()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                buildAnnotatedString {
                    append("Already have an account?")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                        )
                    ) {
                        append(" Sign in")
                    }
                },
                modifier = Modifier.clickable {
                    navController.navigate(AuthScreen.SignIn.route)
                },
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
