package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.logo_emali
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LogoSection(
    title: String,
    subtitle: String
) {
    val darkTheme = isSystemInDarkTheme()
    val logo = if (darkTheme)
        painterResource(Res.drawable.logo_emali)
    else
        painterResource(Res.drawable.logo_emali)


    Image(
        modifier = Modifier.width(125.dp).height(125.dp),
        contentScale = ContentScale.FillWidth,
        painter = logo,
        contentDescription = "Pager Image"
    )
    Text(
        text = title,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge
    )
}