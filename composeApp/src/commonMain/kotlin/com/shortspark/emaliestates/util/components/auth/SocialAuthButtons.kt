package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.google // Assuming you have these in your resources
import emaliestates.composeapp.generated.resources.facebook // Assuming you have these in your resources

// Define Google button specific colors
object GoogleButtonColors {
    val DefaultContainerColor = Color.Transparent // White background
    val DefaultContentColor = Color(0xFF3C4043) // Dark gray text/icon color
    val DefaultBorderColor = Color(0xFFD1D1D1) // Light gray border
}

@Composable
fun SocialAuthButtons(
    modifier: Modifier = Modifier,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GoogleSignInButton(onClick = onGoogleClick)
        FacebookSignInButton(onClick = onFacebookClick)
    }
}

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth() // Adjust width as needed, 0.8f means 80% of parent width
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = if (darkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary, // Dark theme),
            // borderColor = GoogleButtonColors.DefaultBorderColor // OutlinedButton implicitly uses contentColor for border if not set
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)), // Explicitly set border
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.google), // Replace with your Google icon resource
                contentDescription = "Google Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified // Use the icon's original color
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

@Composable
fun FacebookSignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
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
}