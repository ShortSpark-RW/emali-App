package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.viewModel.FacebookAuthViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.util.components.common.AnimatedMessage
import com.shortspark.emaliestates.util.components.common.MessageType
import com.sunildhiman90.kmauth.supabase.KMAuthSupabase
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.facebook
import emaliestates.composeapp.generated.resources.google
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SocialAuthButtons(
    modifier: Modifier = Modifier,
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Show error message if any
        AnimatedMessage(
            message = authState.errorMessage ?: "",
            type = MessageType.ERROR,
            isVisible = authState.errorMessage != null,
            onAutoDismiss = { authViewModel.clearError() }
        )

        GoogleSignInButton(
            authViewModel = authViewModel,
            isLoggedIn = authState.currentUser != null,
            isLoading = authState.isLoading && authState.operation == com.shortspark.emaliestates.auth.viewModel.AuthOperation.GoogleSignIn
        )
        // FacebookSignInButton()
    }
}

@Composable
fun GoogleSignInButton(
    authViewModel: AuthViewModel,
    isLoggedIn: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {

    OutlinedButton(
        onClick = {
            if (isLoggedIn) {
                authViewModel.logout()
            } else {
                authViewModel.signInWithGoogle()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
    ) {
        if (isLoading) {
            // Show loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else if (isLoggedIn) {
            Text(
                text = "Sign out",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun FacebookSignInButton(
    modifier: Modifier = Modifier,
) {

    val facebookAuthViewModel = remember {
        FacebookAuthViewModel(
            facebookAuthManager = KMAuthSupabase.getAuthManager()
        )
    }

    val state by facebookAuthViewModel.facebookAuthUiState.collectAsState()

    val onSignIn = {
        facebookAuthViewModel.signInWithFacebook()
    }

    val onSignOut = {
        facebookAuthViewModel.signOut()
    }

    if (state.user == null && state.errorMessage == null) {
        OutlinedButton(
            onClick = {
                onSignIn()
            },
            modifier = modifier
                .fillMaxWidth() // Adjust width as needed
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Facebook blue
                contentColor = Color.White,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.facebook), // Replace with your Facebook icon resource
                    contentDescription = "Facebook Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified // Use the icon's original color
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Facebook",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = {
                onSignOut()
            },
            modifier = modifier
                .fillMaxWidth() // Adjust width as needed
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, // Facebook blue
                contentColor = Color.White,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sign out, ${state.user}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}