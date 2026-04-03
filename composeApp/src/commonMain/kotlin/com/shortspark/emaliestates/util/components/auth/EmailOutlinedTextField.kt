package com.shortspark.emaliestates.util.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.email_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmailOutlinedTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    isEmailFocused: Boolean,
    onFocusChange: (FocusState) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    imeAction: ImeAction,
    isError: Boolean = false
) {
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email address") },
            placeholder = {
                Text(
                    text = "Type your email address",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 12.sp,
                        fontSize = 14.sp,
                    ),
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.email_icon),
                        contentDescription = "Email Icon",
                        tint = if (isEmailFocused) MaterialTheme.colorScheme.secondary
                        else if (isEmailFocused && isError) MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState)
                },
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.secondary,

                errorBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 12.sp,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions {
                defaultKeyboardAction(ImeAction.Next)
            },
            isError = isError,
        )
        if (isError) {
            FieldSubText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}