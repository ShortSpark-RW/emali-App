package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.facebook
import emaliestates.composeapp.generated.resources.google
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun SocialAuthButtons(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialAuthButton(
            text = "Google",
            icon = painterResource(Res.drawable.google), // Your Google icon
            backgroundColor = Color.White,
            textColor = Color.Black,
            borderColor = Color.LightGray,
            onClick = onGoogleClick
        )

        SocialAuthButton(
            text = "Facebook",
            icon = painterResource(Res.drawable.facebook), // Your Facebook icon
            backgroundColor = Color(0xFF1877F2),
            textColor = Color.White,
            borderColor = Color.Transparent,
            onClick = onFacebookClick
        )
    }
}