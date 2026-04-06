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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.viewModel.VerifyOtpViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OtpInput
import com.shortspark.emaliestates.util.components.common.AnimatedMessage
import com.shortspark.emaliestates.util.components.common.MessageType
import com.shortspark.emaliestates.util.components.common.AppButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VerifyOtpScreen(
    navController: NavController
) {
    // Get email from previous back stack entry's savedStateHandle
    val email = navController.previousBackStackEntry?.savedStateHandle?.get<String>("email")
        ?: run {
            // If email is missing, show error and maybe go back
            Box(modifier = Modifier.fillMaxSize()) {
                // Could show a message
            }
            return
        }

    val viewModel: VerifyOtpViewModel = koinViewModel(parameters = { parametersOf(email) })
    val authViewModel: AuthViewModel = koinViewModel()

    val otpError = viewModel.otpError
    val verifyState = viewModel.verifyState.value
    val isLoading = verifyState.isLoading()

    // Navigate to home when user becomes authenticated (after OTP verification)
    LaunchedEffect(authViewModel.authState) {
        if (authViewModel.authState.value.currentUser != null) {
            navController.navigate(Graph.BASE) {
                popUpTo(Graph.AUTHENTICATION) { inclusive = true }
            }
        }
    }

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
                        title = "Verify email",
                        subtitle = "Enter the verification code sent to $email"
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                OtpInput(
                    length = 6,
                    onComplete = { code ->
                        viewModel.updateOtp(code)
                    }
                )

                AnimatedMessage(
                    message = otpError ?: "",
                    type = MessageType.ERROR,
                    isVisible = otpError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    buildAnnotatedString {
                        append("Code expires in ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("5:00 ")
                        }
                        append("minutes")
                    }
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
                            append(" Resend code")
                        }
                    },
                    modifier = Modifier.clickable {
                        // TODO: Implement resend
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
                    text = if (isLoading) "Verifying..." else "Verify email address",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    loading = isLoading,
                    onClick = {
                        viewModel.verifyOtp()
                    }
                )
            }
        }
    }
}
