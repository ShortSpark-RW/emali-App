package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.common.AppButton
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.email_icon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun Signup2Screen(
    navController: NavController,
//    viewModel: AuthViewModel = hiltViewModel()
) {

    Signup2Content(navController)
}

@Composable
@Preview(showBackground = true)
fun Signup2Content(
    navController: NavController = rememberNavController(),
) {
    var fullname by remember { mutableStateOf("") }
    var isFullnameFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            LogoSection(
                subtitle = "Please enter your details"
            )

            Spacer(modifier = Modifier.height(8.dp))

            CommonOutlinedTextField(
                value = fullname,
                onValueChange = { fullname = it },
                isFocused = isFullnameFocused,
                onFocusChange = { isFullnameFocused = it.isFocused },
                label = "Full Name",
                placeholder = "Type your full name",
                leadingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.email_icon),
                            contentDescription = "Fullname Icon",
                            tint = if (isFullnameFocused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        )
                    }
                },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Field 2")

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(6.dp))

            AppButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                text = "Next",
                textColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                onClick = {
                    navController.navigate(BaseScreen.Home.route)
                }
            )
        }
    }
}
