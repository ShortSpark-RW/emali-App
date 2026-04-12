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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.navigation.NavGraph
import com.shortspark.emaliestates.navigation.Screen
import com.shortspark.emaliestates.util.components.auth.EmailOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OrDivider
import com.shortspark.emaliestates.util.components.auth.PasswordOutlinedTextField
import com.shortspark.emaliestates.util.components.auth.SocialAuthButtons
import com.shortspark.emaliestates.util.components.common.AppButton
import com.shortspark.emaliestates.auth.viewModel.SignupViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.CircularProgressIndicator
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel


@Composable
fun SignupScreen(
    navController: NavController,
) {
    val parentEntry = remember(navController) { navController.getBackStackEntry(NavGraph.Auth) }
    val viewModel: SignupViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        SignupContent(navController, viewModel)

        // Unified loading overlay
        if (authState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun SignupContent(
    navController: NavController = rememberNavController(),
    viewModel: SignupViewModel
) {
    // Use ViewModel state
    val email = viewModel.email
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val termsAccepted = viewModel.termsAccepted
    val passwordVisibility = remember { mutableStateOf(false) }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }

    // Validation errors from ViewModel
    val emailError = viewModel.emailError
    val passwordError = viewModel.passwordError
    val confirmPasswordError = viewModel.confirmPasswordError
    val termsError = viewModel.termsError

    // UI-only focus states
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }

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

            EmailOutlinedTextField(
                email = email,
                onEmailChange = { viewModel.updateEmail(it) },
                isEmailFocused = isEmailFocused,
                onFocusChange = { isEmailFocused = it.isFocused },
                imeAction = ImeAction.Next,
                isError = emailError != null,
                errorMessage = emailError ?: ""
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordOutlinedTextField(
                value = password,
                onValueChange = { viewModel.updatePassword(it) },
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
                passwordVisibility = passwordVisibility.value,
                onVisibilityChange = { passwordVisibility.value = it },
                isPasswordFocused = isPasswordFocused,
                imeAction = ImeAction.Next,
                onFocusChange = { isPasswordFocused = it.isFocused },
                isError = passwordError != null,
                errorMessage = passwordError ?: ""
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordOutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = "Confirm Password",
                placeholder = "Confirm your password",
                modifier = Modifier.fillMaxWidth(),
                passwordVisibility = confirmPasswordVisibility.value,
                onVisibilityChange = { confirmPasswordVisibility.value = it },
                isPasswordFocused = isConfirmPasswordFocused,
                imeAction = ImeAction.Done,
                onFocusChange = { isConfirmPasswordFocused = it.isFocused },
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError ?: ""
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            modifier = Modifier.padding(end = 2.dp),
                            checked = termsAccepted,
                            onCheckedChange = {
                                viewModel.updateTermsAccepted(it)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.secondary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                checkmarkColor = MaterialTheme.colorScheme.primary,
                                disabledUncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                disabledCheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                disabledIndeterminateColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            ),
                        )
                        Text(
                            text = "I accept the terms and conditions",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "Forgot Password?",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Auth.ForgotPassword)
                        }
                    )
                }
                if (termsError != null) {
                    Text(
                        text = termsError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 4.dp)
                    )
                }
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
                    if (viewModel.validateStep1()) {
                        navController.navigate(Screen.Auth.SignUp2)
                    }
                }
            )

            OrDivider()

            SocialAuthButtons()

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                buildAnnotatedString {
                    append("Already have an accounrt?")
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
                    navController.navigate(Screen.Auth.SignIn)
                },
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
