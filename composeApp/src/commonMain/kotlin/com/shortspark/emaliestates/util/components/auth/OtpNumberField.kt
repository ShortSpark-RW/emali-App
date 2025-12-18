package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OtpInput(
    length: Int = 5,
    onComplete: (String) -> Unit
) {
    var otp by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .clickable { focusRequester.requestFocus() }
    ) {

        // Hidden unified input
        BasicTextField(
            value = otp,
            onValueChange = {
                if (it.length <= length && it.all(Char::isDigit)) {
                    otp = it
                    if (it.length == length) onComplete(it)
                }
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .alpha(0f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),
            singleLine = true
        )

        // Visual boxes
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(length) { index ->
                OtpBox(
                    char = otp.getOrNull(index)?.toString() ?: "",
                    isFocused = otp.length == index,
                )
            }
        }
    }
}

@Composable
private fun OtpBox(
    char: String,
    isFocused: Boolean,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .border(
                width = 1.5.dp,
                color = if (isFocused) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
