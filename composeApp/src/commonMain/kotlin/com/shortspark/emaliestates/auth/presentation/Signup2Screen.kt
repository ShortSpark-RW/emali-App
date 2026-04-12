package com.shortspark.emaliestates.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.viewModel.SignupViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.NavGraph
import com.shortspark.emaliestates.navigation.Screen
import com.shortspark.emaliestates.util.components.auth.CalendarDatePicker
import com.shortspark.emaliestates.util.components.auth.Gender
import com.shortspark.emaliestates.util.components.auth.LogoSection
import com.shortspark.emaliestates.util.components.auth.PhoneNumberOutlinedTextField
import com.shortspark.emaliestates.util.components.common.AnimatedMessage
import com.shortspark.emaliestates.util.components.common.AppButton
import com.shortspark.emaliestates.util.components.common.CommonOutlinedTextField
import com.shortspark.emaliestates.util.components.common.MessageType
import emaliestates.composeapp.generated.resources.Res
import emaliestates.composeapp.generated.resources.full_name
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun Signup2Screen(
    navController: NavController,
) {
    val parentEntry = remember(navController) { navController.getBackStackEntry(NavGraph.Auth) }
    val viewModel: SignupViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.authState.collectAsState()

    // State for date picker dialog
    var showDatePicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Signup2Content(
            navController = navController,
            viewModel = viewModel,
            onDateClick = { showDatePicker = true },
            selectedDate = LocalDate(viewModel.year, viewModel.month, viewModel.day)
        )

        // Unified loading overlay
        if (authState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    strokeWidth = 3.dp
                )
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            Dialog(
                onDismissRequest = { showDatePicker = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                CalendarDatePicker(
                    initialDate = LocalDate(viewModel.year, viewModel.month, viewModel.day),
                    onDateSelected = { date ->
                        viewModel.updateDateOfBirth(date.dayOfMonth, date.monthNumber, date.year)
                        showDatePicker = false
                    },
                    minDate = LocalDate(1900, 1, 1),
                    maxDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
                )
            }
        }
    }
}

@Composable
fun Signup2Content(
    navController: NavController = rememberNavController(),
    viewModel: SignupViewModel,
    onDateClick: () -> Unit,
    selectedDate: kotlinx.datetime.LocalDate
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
    val dateError = viewModel.dateError

    // UI-only focus states
    var isFullnameFocused by remember { mutableStateOf(false) }
    var isPhoneNumberFocused by remember { mutableStateOf(false) }

    // Format date for display
    val dateDisplay = remember(day, month, year) {
        "$day/${month.toString().padStart(2, '0')}/$year"
    }

    val signupState = viewModel.signupState.value
    val signupError = (signupState as? RequestState.Error)?.message

    LaunchedEffect(signupState) {
        if (signupState is RequestState.Success) {
            navController.navigate(Screen.Auth.VerifyOtp)
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

            // Progress indicator
            Text(
                text = "Step 2 of 2",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LogoSection(
                subtitle = "Personal Information"
            )

            // Back button
            Text(
                text = "← Back to account details",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
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

            // Gender selection
            Text(
                text = "Gender",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Gender.entries.forEach { genderOption ->
                    val isSelected = gender == genderOption
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { viewModel.updateGender(genderOption) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.updateGender(genderOption) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.onSecondary,
                                unselectedColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = genderOption.name.lowercase().capitalize(),
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date of Birth - Full row button style
            Text(
                text = "Date of Birth",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onDateClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (dateError != null) dateError!! else dateDisplay.ifEmpty { "DD/MM/YYYY" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        dateError != null -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onBackground
                    }
                )
                Icon(
                    painter = painterResource(Res.drawable.full_name), // Replace with calendar icon
                    contentDescription = "Select Date",
                    tint = if (dateError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
            }
            if (dateError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
            }

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
