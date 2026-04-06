package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing different auth operations for better state tracking
 */
sealed class AuthOperation {
    object Idle : AuthOperation()
    object Login : AuthOperation()
    object Signup : AuthOperation()
    object GoogleSignIn : AuthOperation()
    object VerifyOtp : AuthOperation()
    object ResendOtp : AuthOperation()
    object ForgotPassword : AuthOperation()
    object ChangePassword : AuthOperation()
    object UpdateProfile : AuthOperation()
    object Logout : AuthOperation()
    object RefreshToken : AuthOperation()
}

/**
 * Comprehensive auth state that tracks current operation and overall auth status
 */
data class AuthState(
    val operation: AuthOperation = AuthOperation.Idle,
    val loginState: RequestState<*> = RequestState.Idle,
    val signupState: RequestState<*> = RequestState.Idle,
    val verifyOtpState: RequestState<*> = RequestState.Idle,
    val resendOtpState: RequestState<*> = RequestState.Idle,
    val forgotPasswordState: RequestState<*> = RequestState.Idle,
    val changePasswordState: RequestState<*> = RequestState.Idle,
    val updateProfileState: RequestState<*> = RequestState.Idle,
    val refreshTokenState: RequestState<*> = RequestState.Idle,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val sdk: AuthSDK,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Convenience properties
    val currentUser: User? get() = _authState.value.currentUser
    val isLoading: Boolean get() = _authState.value.isLoading
    val errorMessage: String? get() = _authState.value.errorMessage

    private fun updateState(block: AuthState.() -> AuthState) {
        _authState.value = _authState.value.block()
    }

    // ==================== Email/Password Authentication ====================

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        updateState { copy(operation = AuthOperation.Login, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.login(email, password)
            when (result) {
                is RequestState.Success<*> -> {
                    val user = result.data as? User
                    updateState {
                        copy(
                            loginState = result,
                            currentUser = user,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            loginState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Sign up new user
     */
    fun signup(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        gender: String,
        dateOfBirth: String? = null
    ) {
        updateState { copy(operation = AuthOperation.Signup, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.signup(email, password, fullName, phone, gender, dateOfBirth)
            when (result) {
                is RequestState.Success<*> -> {
                    updateState {
                        copy(
                            signupState = result,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            signupState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Verify OTP after signup or forgot password
     */
    fun verifyOtp(email: String, otp: String) {
        updateState { copy(operation = AuthOperation.VerifyOtp, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.verifyOtp(email, otp)
            when (result) {
                is RequestState.Success<*> -> {
                    val user = result.data as? User
                    updateState {
                        copy(
                            verifyOtpState = result,
                            currentUser = user,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            verifyOtpState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Resend OTP verification code
     */
    fun resendOtp(email: String) {
        updateState { copy(operation = AuthOperation.ResendOtp, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.resendOtp(email)
            when (result) {
                is RequestState.Success<*> -> {
                    updateState {
                        copy(
                            resendOtpState = result,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            resendOtpState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Request password reset
     */
    fun forgotPassword(email: String) {
        updateState { copy(operation = AuthOperation.ForgotPassword, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.forgotPassword(email)
            when (result) {
                is RequestState.Success<*> -> {
                    updateState {
                        copy(
                            forgotPasswordState = result,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            forgotPasswordState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Change password (when user is logged in)
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        updateState { copy(operation = AuthOperation.ChangePassword, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.changePassword(currentPassword, newPassword)
            when (result) {
                is RequestState.Success<*> -> {
                    updateState {
                        copy(
                            changePasswordState = result,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            changePasswordState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    /**
     * Update user profile
     */
    fun updateProfile(
        fullName: String? = null,
        phone: String? = null,
        gender: String? = null,
        dateOfBirth: String? = null,
        locationId: String? = null
    ) {
        updateState { copy(operation = AuthOperation.UpdateProfile, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.updateProfile(fullName, phone, gender, dateOfBirth, locationId)
            when (result) {
                is RequestState.Success<*> -> {
                    val user = result.data as? User
                    updateState {
                        copy(
                            updateProfileState = result,
                            currentUser = user,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    updateState {
                        copy(
                            updateProfileState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    // ==================== Social Authentication ====================

    /**
     * Sign in with Google using KMAuth
     */
    fun signInWithGoogle() {
        updateState { copy(operation = AuthOperation.GoogleSignIn, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                googleAuthManager.signIn { googleUser, error ->
                    if (error == null && googleUser != null) {
                        val idToken = googleUser.idToken
                        if (idToken.isNullOrEmpty()) {
                            updateState {
                                copy(
                                    isLoading = false,
                                    errorMessage = "Google sign-in succeeded but no ID token was received.",
                                    operation = AuthOperation.Idle
                                )
                            }
                            return@signIn
                        }

                        // Send ID token to backend
                        viewModelScope.launch {
                            val backendResult = sdk.google(idToken)
                            when (backendResult) {
                                is RequestState.Success<*> -> {
                                    val user = backendResult.data as? User
                                    updateState {
                                        copy(
                                            currentUser = user,
                                            isLoading = false,
                                            operation = AuthOperation.Idle
                                        )
                                    }
                                }
                                is RequestState.Error -> {
                                    updateState {
                                        copy(
                                            isLoading = false,
                                            errorMessage = backendResult.message,
                                            operation = AuthOperation.Idle
                                        )
                                    }
                                }
                                else -> {
                                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                                }
                            }
                        }
                    } else {
                        handleAuthError(error?.message ?: "Google Sign-In failed", AuthOperation.GoogleSignIn)
                    }
                }
            } catch (e: Exception) {
                handleAuthError(e.message ?: "Google Sign-In error", AuthOperation.GoogleSignIn)
            }
        }
    }

    /**
     * Sign out from all services
     */
    fun logout() {
        updateState { copy(operation = AuthOperation.Logout, isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. Sign out from Google
                try {
                    googleAuthManager.signOut(null)
                } catch (e: Exception) {
                    // Ignore Google sign-out errors
                    e.printStackTrace()
                }

                // 2. Clear local session and tokens
                sdk.logout()

                // 3. Reset all states
                updateState {
                    AuthState(
                        operation = AuthOperation.Idle,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                updateState {
                    AuthState(
                        operation = AuthOperation.Idle,
                        isLoading = false,
                        errorMessage = e.message ?: "Logout failed"
                    )
                }
            }
        }
    }

    /**
     * Refresh access token if needed
     */
    fun refreshToken() {
        updateState { copy(operation = AuthOperation.RefreshToken, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.refreshToken()
            when (result) {
                is RequestState.Success<*> -> {
                    updateState {
                        copy(
                            refreshTokenState = result,
                            isLoading = false,
                            operation = AuthOperation.Idle
                        )
                    }
                }
                is RequestState.Error -> {
                    // Token refresh failed, user needs to re-login
                    updateState {
                        copy(
                            refreshTokenState = result,
                            isLoading = false,
                            errorMessage = result.message,
                            currentUser = null,
                            operation = AuthOperation.Idle
                        )
                    }
                    // Clear session
                    sdk.logout()
                }
                else -> {
                    updateState { copy(isLoading = false, operation = AuthOperation.Idle) }
                }
            }
        }
    }

    // ==================== Error Handling ====================

    private fun handleAuthError(message: String, operation: AuthOperation) {
        // Check if it's a user cancellation
        val isCancellation = message.contains("cancel", ignoreCase = true)
            || message.contains("cancelled", ignoreCase = true)
            || message.contains("GetCredentialCancellationException", ignoreCase = true)

        if (isCancellation && operation == AuthOperation.GoogleSignIn) {
            // User cancelled Google sign-in - reset to idle without error
            updateState {
                AuthState(
                    operation = AuthOperation.Idle,
                    isLoading = false
                )
            }
        } else {
            updateState {
                AuthState(
                    operation = operation,
                    isLoading = false,
                    errorMessage = message
                )
            }
        }
    }

    /**
     * Clear specific error message (e.g., after showing it to user)
     */
    fun clearError() {
        updateState { copy(errorMessage = null) }
    }

    /**
     * Reset all states to initial
     */
    fun resetStates() {
        _authState.value = AuthState()
    }

    /**
     * Update current user (used by other ViewModels like VerifyOtpViewModel)
     */
    fun setCurrentUser(user: User?) {
        updateState { copy(currentUser = user) }
    }
}
