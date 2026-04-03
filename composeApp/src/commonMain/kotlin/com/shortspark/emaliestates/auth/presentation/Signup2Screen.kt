package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.domain.Countries
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.AuthScreen
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.util.components.auth.Gender
import com.shortspark.emaliestates.util.components.auth.GenderAndDobRow
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.PhoneNumberOutlinedTextField
import com.shortspark.emaliestates.util.components.common.AppButton
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.full_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun Signup2Screen(
    navController: NavController,
) {
    Signup2Content(navController)
}


@Composable
@Preview(showBackground = true)
fun Signup2Content(
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val signupState by authViewModel.signupState.collectAsState()
    val credentials by authViewModel.signupCredentials.collectAsState()

    // Listen for signup success and navigate
    LaunchedEffect(signupState) {
        if (signupState is RequestState.Success) {
            navController.navigate(Graph.BASE) {
                popUpTo(Graph.AUTHENTICATION) { inclusive = true }
            }
            authViewModel.clearSignupCredentials()
            authViewModel.resetSignupState()
        }
    }

    // Display error if any
    val signupError = (signupState as? RequestState.Error)?.message

    // If no credentials, cannot proceed
    if (credentials == null) {
        androidx.compose.material3.Text(
            text = "Missing email or password. Please go back and fill the previous step.",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
        return
    }
    val (email, password) = credentials!!

    var fullname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(Countries[144]) }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var day by remember { mutableStateOf(15) }
    var month by remember { mutableStateOf(5) }
    var year by remember { mutableStateOf(1994) }

    var fullnameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf<String?>(null)

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

            if (signupError != null) {
                androidx.compose.material3.Text(
                    text = signupError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Full Name field with validation
            CommonOutlinedTextField(
                value = fullname,
                onValueChange = {
                    fullname = it
                    fullnameError = if (it.isBlank()) "Full name is required" else null
                },
                isFocused = false, // TODO: add focus state if needed
                onFocusChange = {},
                label = "Full Name",
                placeholder = "Type your full name",
                leadingIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(Res.drawable.full_name),
                            contentDescription = "Fullname Icon",
                            tint = if (false) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        )
                    }
                },
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                imeAction = ImeAction.Next,
            errorMessage = fullnameError ?: "",
                isError = fullnameError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Number field with validation
            PhoneNumberOutlinedTextField(
                phoneNumber = phoneNumber,
                onPhoneNumberChange = {
                    phoneNumber = it
                    phoneError = if (it.isBlank()) "Phone number is required" else null
                },
                selectedCountry = selectedCountry,
                onCountrySelected = { newCountry ->
                    selectedCountry = newCountry
                },
                isFocused = false,
                onFocusChange = {},
                errorMessage = phoneError ?: "",
                isError = phoneError != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            GenderAndDobRow(
                gender = gender,
                onGenderChange = { gender = it },
                day = day,
                month = month,
                year = year,
                onDayChange = { day = it },
                onMonthChange = { month = it },
                onYearChange = { year = it }
            )

            Spacer(modifier = Modifier.height(6.dp))

            AppButton(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                text = "Register",
                textColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                loading = signupState is RequestState.Loading,
                onClick = {
                    // Validate required fields
                    val fullErr = if (fullname.isBlank()) "Full name is required" else null
                    val phoneErr = if (phoneNumber.isBlank()) "Phone number is required" else null

                    fullnameError = fullErr
                    phoneError = phoneErr

                    if (fullErr != null || phoneErr != null) {
                        return@AppButton
                    }

                    // Call signup with all collected data
                    authViewModel.signup(
                        email = email,
                        password = password,
                        fullName = fullname,
                        phone = phoneNumber,
                        gender = gender.name.lowercase(),
                        dob = "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}",
                        username = null // Could use email or fullname as username if needed
                    )
                }
            )
        }
    }
}
