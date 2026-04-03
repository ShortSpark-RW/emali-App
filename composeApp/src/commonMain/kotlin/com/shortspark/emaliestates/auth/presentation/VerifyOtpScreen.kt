package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OtpInput
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun VerifyOtpScreen(
    navController: NavController
) {
    VerifyOtpContent(navController)
}


@Composable
@Preview(showBackground = true)
fun VerifyOtpContent(
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val verifyOtpState by authViewModel.verifyOtpState.collectAsState()
    val refreshOtpState by authViewModel.refreshOtpState.collectAsState()
    val verificationEmail by authViewModel.verificationEmail.collectAsState()

    LaunchedEffect(verifyOtpState) {
        if (verifyOtpState is RequestState.Success) {
            // OTP verified, proceed to change password
            navController.navigate(AuthScreen.ChangePassword.route)
            // Reset state to avoid re-trigger when coming back
            authViewModel.resetVerifyOtpState()
        }
    }

    LaunchedEffect(refreshOtpState) {
        if (refreshOtpState is RequestState.Success) {
            // OTP resent, could show a message
            authViewModel.resetRefreshOtpState()
        }
    }

    val verifyOtpError = (verifyOtpState as? RequestState.Error)?.message
    val refreshOtpError = (refreshOtpState as? RequestState.Error)?.message

    // If no email in ViewModel, something went wrong; show error
    val email = verificationEmail
    if (email == null) {
        androidx.compose.material3.Text(
            text = "No email address found. Please go back and try again.",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    var otp by remember { mutableStateOf("") }

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
                        subtitle = "Verify your email below to proceed"
                    )

                    if (verifyOtpError != null) {
                        androidx.compose.material3.Text(
                            text = verifyOtpError,
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

                OtpInput(
                    length = 5,
                    onComplete = {
                        otp = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                val refreshError = refreshOtpState as? RequestState.Error
                if (refreshError != null) {
                    androidx.compose.material3.Text(
                        text = refreshError.message ?: "Failed to resend code",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

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

                val isLoadingResend = refreshOtpState is RequestState.Loading
                Text(
                    buildAnnotatedString {
                        append("Didn't receive a code?")
                        withStyle(
                            style = SpanStyle(
                                color = if (isLoadingResend) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Normal,
                            )
                        ) {
                            append(" Resend code")
                        }
                    },
                    modifier = Modifier.clickable(enabled = !isLoadingResend) {
                        authViewModel.refreshOtp(email)
                    },
                    color = if (isLoadingResend) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    text = "Verify email address",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    loading = verifyOtpState is RequestState.Loading,
                    onClick = {
                        if (otp.length == 5) {
                            authViewModel.verifyOtp(email, otp)
                        } else {
                            // Could show error
                            return@AppButton
                        }
                    }
                )
            }
        }
    }
}
