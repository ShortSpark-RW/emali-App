package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sdk: AuthSDK,
    private val googleAuthManager: GoogleAuthManager,
    private val settings: Settings
) : ViewModel() {

    companion object {
        private const val KEY_REMEMBERED_EMAIL = "remembered_email"
    }

    // Unified State: Tracks loading, success (with User), or error for ANY login method
    private val _loginState = MutableStateFlow<RequestState<User>>(RequestState.Idle)
    val loginState = _loginState.asStateFlow()

    // Signup State
    private val _signupState = MutableStateFlow<RequestState<User>>(RequestState.Idle)
    val signupState = _signupState.asStateFlow()

    // Forgot Password State
    private val _forgotPasswordState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val forgotPasswordState = _forgotPasswordState.asStateFlow()

    // Verify OTP State
    private val _verifyOtpState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val verifyOtpState = _verifyOtpState.asStateFlow()

    // Reset Password State
    private val _resetPasswordState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    // Refresh OTP State (for resend)
    private val _refreshOtpState = MutableStateFlow<RequestState<Unit>>(RequestState.Idle)
    val refreshOtpState = _refreshOtpState.asStateFlow()

    // Temporary signup credentials storage (email, password) for multi-step signup
    private val _signupCredentials = MutableStateFlow<Pair<String, String>?>(null)
    val signupCredentials = _signupCredentials.asStateFlow()

    // Email for verification (forgot password flow)
    private val _verificationEmail = MutableStateFlow<String?>(null)
    val verificationEmail = _verificationEmail.asStateFlow()

    // Remembered email from "Remember Me" feature
    val rememberedEmail: String? = settings.getStringOrNull(KEY_REMEMBERED_EMAIL)

    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        // Load current user from local db if available
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = sdk.getCurrentUser()
        }
    }

    // --- Standard Email/Password Login ---
    fun login(email: String, password: String, rememberMe: Boolean = false) {
        _loginState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.login(email, password)
            updateState(result)

            // If login successful and rememberMe checked, save email
            if (result is RequestState.Success && rememberMe) {
                settings.putString(KEY_REMEMBERED_EMAIL, email)
            } else if (result is RequestState.Error) {
                // On error, ensure we don't have stale remembered email?
                // Actually keep it for user convenience.
            }
        }
    }

    // --- Google Sign In ---
    fun signInWithGoogle() {
        _loginState.update { RequestState.Loading }

        viewModelScope.launch {
            try {
                // 1. Trigger the native Google Sign-In prompt
                googleAuthManager.signIn { googleUser, error ->
                    if (error == null && googleUser != null) {
                        // 2. If Google sign-in succeeds, validate the ID Token
                        val idToken = googleUser.idToken
                        if (idToken.isNullOrBlank()) {
                            _loginState.update { RequestState.Error("Google ID token is missing. Please try again.") }
                            return@signIn
                        }

                        // 3. Send the ID Token to your backend
                        viewModelScope.launch {
                            val backendResult = sdk.google(idToken)
                            updateState(backendResult)
                        }
                    } else {
                        // Check if this is a user cancellation - don't show as error
                        val errorMsg = error?.message ?: ""
                        val isCancellation = errorMsg.contains("cancelled", ignoreCase = true) ||
                            errorMsg.contains("canceled", ignoreCase = true) ||
                            error?.javaClass?.simpleName?.contains("Cancellation", ignoreCase = true) == true

                        if (isCancellation) {
                            // User cancelled - reset to idle without showing error
                            _loginState.update { RequestState.Idle }
                        } else {
                            _loginState.update { RequestState.Error(errorMsg.ifBlank { "Google Sign-In failed" }) }
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions that might occur during the signIn call itself
                val errorMsg = e.message ?: "Unknown Google Auth Error"
                // Check if it's a cancellation exception
                val isCancellation = e.javaClass.simpleName.contains("Cancellation", ignoreCase = true)
                if (isCancellation) {
                    _loginState.update { RequestState.Idle }
                } else {
                    _loginState.update { RequestState.Error(errorMsg) }
                }
            }
        }
    }

    // --- Unified Logout ---
    fun logout() {
        _loginState.update { RequestState.Loading }

        viewModelScope.launch {
            // 1. Sign out of Google (prevent auto-login next time)
            try {
                googleAuthManager.signOut(null)
            } catch (e: Exception) {
                // Ignore errors here (e.g. if user wasn't signed in to Google)
            }

            // 2. Clear Local Database & Session
            sdk.logout()

            // 3. Clear remembered email
            settings.remove(KEY_REMEMBERED_EMAIL)

            // 4. Clear current user
            _currentUser.value = null

            // 5. Reset State to Idle
            _loginState.update { RequestState.Idle }
        }
    }

    // --- Signup ---
    fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String? = null,
        gender: String? = null,
        dob: String? = null,
        username: String? = null
    ) {
        _signupState.update { RequestState.Loading }

        viewModelScope.launch {
            val request = com.shortspark.emaliestates.domain.auth.SignupRequest(
                email = email,
                password = password,
                fullName = fullName,
                phone = phone,
                gender = gender,
                dob = dob,
                username = username
            )
            val result = sdk.signup(request)
            _signupState.value = result
            if (result is RequestState.Success) {
                _currentUser.value = result.data
            }
        }
    }

    // --- Forgot Password ---
    fun forgotPassword(email: String) {
        _forgotPasswordState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.forgotPassword(email)
            _forgotPasswordState.value = result
        }
    }

    // --- Verify OTP ---
    fun verifyOtp(email: String, otp: String) {
        _verifyOtpState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.verifyOtp(email, otp)
            _verifyOtpState.value = result
        }
    }

    // --- Reset Password ---
    fun resetPassword(email: String, newPassword: String) {
        _resetPasswordState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.resetPassword(email, newPassword)
            _resetPasswordState.value = result
        }
    }

    // --- Refresh OTP ---
    fun refreshOtp(email: String) {
        _refreshOtpState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.refreshOtp(email)
            _refreshOtpState.value = result
        }
    }

    // --- Signup Credentials Management ---
    fun setSignupCredentials(email: String, password: String) {
        _signupCredentials.value = email to password
    }

    fun clearSignupCredentials() {
        _signupCredentials.value = null
    }

    // --- Verification Email Management ---
    fun setVerificationEmail(email: String?) {
        _verificationEmail.value = email
    }

    fun clearVerificationEmail() {
        _verificationEmail.value = null
    }

    // --- State Reset Methods ---
    fun resetForgotPasswordState() {
        _forgotPasswordState.value = RequestState.Idle
    }

    fun resetVerifyOtpState() {
        _verifyOtpState.value = RequestState.Idle
    }

    fun resetResetPasswordState() {
        _resetPasswordState.value = RequestState.Idle
    }

    fun resetRefreshOtpState() {
        _refreshOtpState.value = RequestState.Idle
    }

    fun resetSignupState() {
        _signupState.value = RequestState.Idle
    }

    // Helper to reduce code duplication
    private fun updateState(result: RequestState<User>) {
        _loginState.value = result
        if (result is RequestState.Success) {
            _currentUser.value = result.data
        }
    }
}