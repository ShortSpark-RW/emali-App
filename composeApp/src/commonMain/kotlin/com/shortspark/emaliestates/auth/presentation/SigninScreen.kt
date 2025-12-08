package com.shortspark.emaliestates.auth.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.Greeting
import com.shortspark.emaliestates.navigation.Screen
import com.shortspark.emaliestates.theme.EmaliEstatesTheme
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.OrDivider
import com.shortspark.emaliestates.util.components.auth.SocialAuthButtons
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.app_name
import emaliestates.composeapp.generated.resources.logo_emali
import emaliestates.composeapp.generated.resources.visibility
import emaliestates.composeapp.generated.resources.visibility_off
import emaliestates.composeapp.generated.resources.welcome_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun SigninScreen(
    navController: NavController,
//    viewModel: AuthViewModel = hiltViewModel()
) {
//    var showContent by remember { mutableStateOf(true) }
//    Column(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.background)
//            .safeContentPadding()
//            .fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Button(onClick = { showContent = !showContent }) {
//            Text(stringResource(Res.string.welcome_message))
//        }
//        AnimatedVisibility(showContent) {
//            val greeting = remember { Greeting().greet() }
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Image(painterResource(Res.drawable.logo_emali), null)
//                Text(stringResource(Res.string.app_name), color = MaterialTheme.colorScheme.onPrimary)
//                Text("Compose: $greeting", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//    }
    SigninContent(navController)
}

@Composable
@Preview
fun SigninContent(
    navController: NavController = rememberNavController(),
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility)
        painterResource(Res.drawable.visibility)
    else
        painterResource(Res.drawable.visibility_off)

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
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text("Enter your username") },
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Email Icon"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text(text = "Password")
            },
            label = { Text(text = "Password") },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Email Icon"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        painter = icon,
                        contentDescription = "Visibility Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
//                    Log.d("ImeAction3", "Password Done Clicked")
                }
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Optional padding
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable {
                    // Handle click here (e.g., navigate to reset screen)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
//            onClick = { onSignIn(username, password) },
            onClick = {
                navController.navigate(Screen.Home.route)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        ) {
            Text("Sign In")
        }

        OrDivider()

        SocialAuthButtons(
            onGoogleClick = {
                // Handle Google login
            },
            onFacebookClick = {},
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            buildAnnotatedString {
                append("Don't have an account?")
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                ) {
                    append(" Sign Up")
                }
            },
            modifier = Modifier.clickable {
                navController.navigate(Screen.SignUp.route)
            }
        )
    }
}