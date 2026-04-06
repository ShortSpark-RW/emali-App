package com.shortspark.emaliestates.util.components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.lock_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun PasswordOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordFocused: Boolean,
    onFocusChange: (FocusState) -> Unit,

    // Optional
    label: String = "Password",
    placeholder: String = "Type your password",
    modifier: Modifier = Modifier,
    passwordVisibility: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    icon: @Composable () -> Unit = {},
    imeAction: ImeAction,
    errorMessage: String = "",
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState)
                },
            enabled = enabled,
            isError = isError,
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 12.sp,
                        fontSize = 14.sp,
                    ),
                )
            },
            label = { Text(text = label) },
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                errorBorderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
            ),
            visualTransformation = if (passwordVisibility)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),

            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.lock_icon),
                        contentDescription = "Lock Icon",
                        tint = if (isPasswordFocused) MaterialTheme.colorScheme.secondary
                        else if (isPasswordFocused && isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },


            trailingIcon = {
                IconButton(onClick = {
                    onVisibilityChange(!passwordVisibility)
//                passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Visibility Icon",
                        tint = if (isPasswordFocused || passwordVisibility) MaterialTheme.colorScheme.secondary
                        else if ((isPasswordFocused || passwordVisibility) && isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            },

            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 12.sp,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                        defaultKeyboardAction(ImeAction.Done)
                }
            ),
//        supportingText = {
//            if (isError) {
//                Text(
//                    text = errorMessage,
//                    color = MaterialTheme.colorScheme.error,
//                )
//            }
//        }
        )

        // Animated error message
        AnimatedVisibility(
            visible = isError && errorMessage.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            FieldSubText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
