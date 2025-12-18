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
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OtpInput
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerifyOtpScreen(
    navController: NavController
) {
    VerifyOtpContent(navController)
}

@Composable
@Preview(showBackground = true)
fun VerifyOtpContent(
    navController: NavController = rememberNavController()
) {



    Box(
        modifier = Modifier.fillMaxSize(),
    ){

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
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                Spacer(modifier = Modifier.height(8.dp))

                OtpInput(
                    length = 5,
                    onComplete = {
                        // Handle OTP completion
                    }
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
//                        navController.navigate(AuthScreen.SignUp.route)
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
                    text = "Verify email address",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        navController.navigate(AuthScreen.ChangePassword.route)
                    }
                )
            }
        }
    }
}