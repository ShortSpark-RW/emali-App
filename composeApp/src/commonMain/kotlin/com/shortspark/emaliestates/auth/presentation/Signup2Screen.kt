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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.domain.Countries
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.util.components.auth.Gender
import com.shortspark.emaliestates.util.components.auth.GenderAndDobRow
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.PhoneNumberOutlinedTextField
import com.shortspark.emaliestates.util.components.common.AppButton
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.email_icon
import emaliestates.composeapp.generated.resources.full_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.shortspark.emaliestates.auth.viewModel.SignupViewModel
import com.shortspark.emaliestates.navigation.Graph
import org.koin.compose.viewmodel.koinViewModel
import com.shortspark.emaliestates.util.components.common.AnimatedMessage
import com.shortspark.emaliestates.util.components.common.MessageType
import com.shortspark.emaliestates.domain.RequestState
// AuthScreen already imported above


@Composable
fun Signup2Screen(
    navController: NavController,
) {
    val parentEntry = remember(navController) { navController.getBackStackEntry(Graph.AUTHENTICATION) }
    val viewModel: SignupViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

    Signup2Content(navController, viewModel)
}

@Composable
fun Signup2Content(
    navController: NavController = rememberNavController(),
    viewModel: SignupViewModel
) {
    // Form state from ViewModel
    val fullName = viewModel.fullName
    val phoneNumber = viewModel.phoneNumber
    val selectedCountry = viewModel.selectedCountry
    val gender = viewModel.gender
    val day = viewModel.day
    val month = viewModel.month
    val year = viewModel.year

    // Validation errors
    val fullNameError = viewModel.fullNameError
    val phoneError = viewModel.phoneError

    // UI-only focus states
    var isFullnameFocused by remember { mutableStateOf(false) }
    var isPhoneNumberFocused by remember { mutableStateOf(false) }



    val signupState = viewModel.signupState.value
    val signupError = (signupState as? RequestState.Error)?.message
    val isLoading = signupState.isLoading()

    LaunchedEffect(signupState) {
        if (signupState is RequestState.Success) {
            // Store email in savedStateHandle for OTP verification
            navController.currentBackStackEntry?.savedStateHandle?.set("email", viewModel.email)
            navController.navigate(AuthScreen.VerifyOtp.route)
        }
    }

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

            // Global error message for signup API failures
            if (signupError != null) {
                AnimatedMessage(
                    message = signupError,
                    type = MessageType.ERROR,
                    onDismiss = { viewModel.resetState() },
                    animationStyle = com.shortspark.emaliestates.util.components.common.MessageAnimationStyle.SLIDE,
                    showDismissButton = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            CommonOutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.updateFullName(it) },
                isFocused = isFullnameFocused,
                onFocusChange = { isFullnameFocused = it.isFocused },
                label = "Full Name",
                placeholder = "Type your full name",
                leadingIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.full_name),
                            contentDescription = "Fullname Icon",
                            tint = if (isFullnameFocused) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        )
                    }
                },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                isError = fullNameError != null
            )
            if (fullNameError != null) {
                Text(
                    text = fullNameError as String,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PhoneNumberOutlinedTextField(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
                selectedCountry = selectedCountry,
                onCountrySelected = { newCountry ->
                    viewModel.updateSelectedCountry(newCountry)
                },
                isFocused = isPhoneNumberFocused,
                onFocusChange = { isPhoneNumberFocused = it.isFocused },
                isError = phoneError != null
            )
            if (phoneError != null) {
                Text(
                    text = phoneError as String,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            GenderAndDobRow(
                gender = gender,
                onGenderChange = { viewModel.updateGender(it) },
                day = day,
                month = month,
                year = year,
                onDayChange = { viewModel.updateDateOfBirth(it, month, year) },
                onMonthChange = { viewModel.updateDateOfBirth(day, it, year) },
                onYearChange = { viewModel.updateDateOfBirth(day, month, it) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            val isLoading = signupState.isLoading()
            AppButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                text = if (isLoading) "Registering..." else "Register",
                textColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                loading = isLoading,
                onClick = {
                    viewModel.signup()
                }
            )
        }
    }
}
