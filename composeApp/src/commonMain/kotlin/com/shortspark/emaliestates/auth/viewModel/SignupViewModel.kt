package com.shortspark.emaliestates.auth.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.util.components.auth.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SignupViewModel(
    private val sdk: AuthSDK,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    // Step 1 fields
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var termsAccepted by mutableStateOf(false)
        private set

    // Step 2 fields
    var fullName by mutableStateOf("")
        private set
    var phoneNumber by mutableStateOf("")
        private set
    var selectedCountry by mutableStateOf(
        com.shortspark.emaliestates.domain.Countries[144]
    )
        private set
    var gender by mutableStateOf(Gender.MALE)
        private set
    var day by mutableStateOf(15)
        private set
    var month by mutableStateOf(5)
        private set
    var year by mutableStateOf(1994)
        private set

    // Validation errors
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set
    var termsError by mutableStateOf<String?>(null)
        private set
    var fullNameError by mutableStateOf<String?>(null)
        private set
    var phoneError by mutableStateOf<String?>(null)
        private set
    var dateError by mutableStateOf<String?>(null)
        private set

    // Signup state
    private val _signupState = mutableStateOf<RequestState<Any>>(RequestState.Idle)
    val signupState = _signupState

    // OTP verification state
    var otp by mutableStateOf("")
        private set
    var otpError by mutableStateOf<String?>(null)
        private set
    private val _verifyState = mutableStateOf<RequestState<Any>>(RequestState.Idle)
    val verifyState = _verifyState

    // Update methods
    fun updateEmail(newEmail: String) {
        email = newEmail
        emailError = null
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        passwordError = null
    }

    fun updateConfirmPassword(newConfirm: String) {
        confirmPassword = newConfirm
        confirmPasswordError = null
    }

    fun updateTermsAccepted(accepted: Boolean) {
        termsAccepted = accepted
        termsError = null
    }

    fun updateFullName(newFullName: String) {
        fullName = newFullName
        fullNameError = null
    }

    fun updatePhoneNumber(newPhone: String) {
        phoneNumber = newPhone
        phoneError = null
    }

    fun updateSelectedCountry(newCountry: com.shortspark.emaliestates.domain.Country) {
        selectedCountry = newCountry
    }

    fun updateGender(newGender: Gender) {
        gender = newGender
    }

    fun updateDateOfBirth(newDay: Int, newMonth: Int, newYear: Int) {
        day = newDay
        month = newMonth
        year = newYear
        dateError = null // Clear error when user changes date
    }

    // Validate Step 1
    fun validateStep1(): Boolean {
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!isValidEmail(email)) {
            emailError = "Please enter a valid email address"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        }

        if (!termsAccepted) {
            termsError = "You must accept the terms and conditions"
            isValid = false
        }

        return isValid
    }

    // Validate Step 2
    fun validateStep2(): Boolean {
        var isValid = true

        // Validate full name
        val trimmedName = fullName.trim()
        when {
            trimmedName.isBlank() -> {
                fullNameError = "Full name is required"
                isValid = false
            }
            trimmedName.length < 2 -> {
                fullNameError = "Name is too short"
                isValid = false
            }
            trimmedName.length > 100 -> {
                fullNameError = "Name is too long (max 100 characters)"
                isValid = false
            }
            !trimmedName.matches(Regex("^[a-zA-Z\\s.'-]+$")) -> {
                fullNameError = "Name contains invalid characters"
                isValid = false
            }
            trimmedName.split("\\s+".toRegex()).filter { it.isNotBlank() }.size < 2 -> {
                fullNameError = "Please enter both first and last name"
                isValid = false
            }
        }

        // Validate phone number - must be digits only after stripping non-digits
        val rawPhone = phoneNumber.filter { it.isDigit() }
        when {
            rawPhone.isBlank() -> {
                phoneError = "Phone number is required"
                isValid = false
            }
            rawPhone.length < 9 -> {
                phoneError = "Phone number is too short"
                isValid = false
            }
            rawPhone.length > 15 -> {
                phoneError = "Phone number is too long"
                isValid = false
            }
            !rawPhone.all { it in '0'..'9' } -> {
                phoneError = "Phone number must contain only digits"
                isValid = false
            }
        }

        // Validate date of birth
        dateError = null
        try {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val birthDate = LocalDate(year, month, day)

            // Check if date is in the future
            if (birthDate > today) {
                dateError = "Date of birth cannot be in the future"
                isValid = false
            }

            // Check minimum age (e.g., 18 years)
            val minAge = 18
            val minBirthDate = today.minus(DatePeriod(years = minAge))
            if (birthDate > minBirthDate) {
                dateError = "You must be at least $minAge years old"
                isValid = false
            }
        } catch (e: Exception) {
            // Invalid date (e.g., February 30)
            dateError = "Invalid date of birth"
            isValid = false
        }

        return isValid
    }

    // Perform signup
    fun signup() {
        if (!validateStep1() || !validateStep2()) {
            return
        }

        // Set global auth state
        authViewModel.setLoading(true)
        authViewModel.setOperation(AuthOperation.Signup)
        authViewModel.setAuthError(null)

        viewModelScope.launch {
            // Format phone number: strip all non-digits from phone input
            val cleanPhoneNumber = phoneNumber.filter { it.isDigit() }
            // Get clean country dial code (digits only, no +)
            val cleanDialCode = selectedCountry.dialCode.filter { it.isDigit() }
            // Combine: full international number without + (e.g., 250781234567)
            // Backend typically accepts E.164 without the plus, or we could include it
            val fullPhone = "$cleanDialCode$cleanPhoneNumber"

            // Validate date of birth
            val dateOfBirth = "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

            val result = sdk.signup(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                phone = fullPhone,
                gender = gender.name,
                dateOfBirth = dateOfBirth
            )

            // Reset global auth state
            authViewModel.setLoading(false)
            authViewModel.setOperation(AuthOperation.Idle)

            if (result is RequestState.Error) {
                authViewModel.setAuthError(result.message)
            }

            _signupState.value = result
        }
    }

    fun resetState() {
        _signupState.value = RequestState.Idle
    }

    fun clearForm() {
        email = ""
        password = ""
        confirmPassword = ""
        termsAccepted = false
        fullName = ""
        phoneNumber = ""
        selectedCountry = com.shortspark.emaliestates.domain.Countries[144]
        gender = Gender.MALE
        day = 15
        month = 5
        year = 1994
        emailError = null
        passwordError = null
        confirmPasswordError = null
        termsError = null
        fullNameError = null
        phoneError = null
        dateError = null
        resetState()
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        val emailStr = email.toString()
        val atIndex = emailStr.indexOf('@')
        val lastDotIndex = emailStr.lastIndexOf('.')
        return atIndex > 0 && lastDotIndex > atIndex + 1 && !emailStr.contains(' ')
    }
}
