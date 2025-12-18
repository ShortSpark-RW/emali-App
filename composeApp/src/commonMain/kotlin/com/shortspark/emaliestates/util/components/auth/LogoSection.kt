package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.logo_emali
import emaliestates.composeapp.generated.resources.logo_image
import emaliestates.composeapp.generated.resources.logo_image_dark
import emaliestates.composeapp.generated.resources.logo_svg
import emaliestates.composeapp.generated.resources.logo_svg_dark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LogoSection(
    title: String? = null,
    subtitle: String
) {
    val darkTheme = isSystemInDarkTheme()
    val logo = if (darkTheme)
        painterResource(Res.drawable.logo_svg_dark)
    else
        painterResource(Res.drawable.logo_svg)


    Image(
        modifier = Modifier.width(120.dp).height(120.dp),
        contentScale = ContentScale.FillWidth,
        painter = logo,
        contentDescription = "Pager Image",
    )
    if (title != null) {
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
            fontSize = MaterialTheme.typography.titleLarge.fontSize
        )
    }
    Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        lineHeight = MaterialTheme.typography.titleSmall.lineHeight
    )
}