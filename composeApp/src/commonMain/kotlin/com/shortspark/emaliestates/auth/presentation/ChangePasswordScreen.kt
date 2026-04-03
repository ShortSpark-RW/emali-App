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
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.PasswordOutlinedTextField
import com.shortspark.emaliestates.util.components.common.AppButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChangePasswordScreen(
    navController: NavController
) {
    ChangePasswordContent(navController)
}

@Composable
@Preview(showBackground = true)
fun ChangePasswordContent(
    navController: NavController = rememberNavController()
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }


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
                    title = "Change Password",
                    subtitle = "Please enter your new password"
                )
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
                        errorMessage = ""
                    },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    passwordVisibility = passwordVisibility,
                    onVisibilityChange = { passwordVisibility = it },
                    isPasswordFocused = isPasswordFocused,
                    imeAction = ImeAction.Next,
                    onFocusChange = { isPasswordFocused = it.isFocused }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordOutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = ""
                    },
                    label = "Confirm Password",
                    placeholder = "Confirm your password",
                    modifier = Modifier.fillMaxWidth(),
                    passwordVisibility = confirmPasswordVisibility,
                    onVisibilityChange = { confirmPasswordVisibility = it },
                    isPasswordFocused = isConfirmPasswordFocused,
                    imeAction = ImeAction.Done,
                    onFocusChange = { isConfirmPasswordFocused = it.isFocused }
                )

                Spacer(modifier = Modifier.height(12.dp))


                AppButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    text = "Change Password",
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        navController.navigate(BaseScreen.Home.route)
                    }
                )
            }

        }
    }
}