package com.shortspark.emaliestates.util.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,

    // Focus
    isFocused: Boolean,
    onFocusChange: (FocusState) -> Unit,

    // Text
    label: String,
    placeholder: String,

    // Icons
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    // Input config
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    visualTransformation: VisualTransformation = VisualTransformation.None,

    // State
    enabled: Boolean = true,
    isError: Boolean = false,

    // Actions
    keyboardActions: KeyboardActions = KeyboardActions.Default,

    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .onFocusChanged { onFocusChange(it) },
        enabled = enabled,
        isError = isError,
        singleLine = true,

        label = { Text(text = label) },
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    lineHeight = 12.sp
                )
            )
        },

        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,

        visualTransformation = visualTransformation,

        shape = RoundedCornerShape(15.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        ),

        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            lineHeight = 12.sp
        ),

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),

        keyboardActions = keyboardActions
    )
}
