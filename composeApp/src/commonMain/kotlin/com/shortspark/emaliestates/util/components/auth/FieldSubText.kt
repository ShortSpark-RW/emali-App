package com.shortspark.emaliestates.util.components.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun FieldSubText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    style: TextStyle = MaterialTheme.typography.bodySmall,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
    )
}