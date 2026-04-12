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
 * ViewModel handling email/password login and Google Sign-In
 */
class LoginViewModel(
    private val sdk: AuthSDK,
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    // UI State for login form
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val rememberMe: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val currentUser: User? = null
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Convenience properties
    val email: String get() = _uiState.value.email
    val password: String get() = _uiState.value.password
    val rememberMe: Boolean get() = _uiState.value.rememberMe
    val emailError: String? get() = _uiState.value.emailError
    val passwordError: String? get() = _uiState.value.passwordError
    val isLoading: Boolean get() = _uiState.value.isLoading
    val errorMessage: String? get() = _uiState.value.errorMessage
    val currentUser: User? get() = _uiState.value.currentUser

    private fun updateUiState(block: LoginUiState.() -> LoginUiState) {
        _uiState.value = _uiState.value.block()
    }

    // ==================== Email/Password Authentication ====================

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        updateUiState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.login(email, password)
            when (result) {
                is RequestState.Success<*> -> {
                    val user = result.data as? User
                    updateUiState {
                        copy(
                            isLoading = false,
                            currentUser = user
                        )
                    }
                }
                is RequestState.Error -> {
                    updateUiState {
                        copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> {
                    updateUiState { copy(isLoading = false) }
                }
            }
        }
    }

    // ==================== Social Authentication ====================

    /**
     * Sign in with Google using KMAuth
     */
    fun signInWithGoogle() {
        updateUiState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                googleAuthManager.signIn { googleUser, error ->
                    if (error == null && googleUser != null) {
                        val idToken = googleUser.idToken
                        if (idToken.isNullOrEmpty()) {
                            updateUiState {
                                copy(
                                    isLoading = false,
                                    errorMessage = "Google sign-in succeeded but no ID token was received."
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
                                    updateUiState {
                                        copy(
                                            currentUser = user,
                                            isLoading = false
                                        )
                                    }
                                }
                                is RequestState.Error -> {
                                    updateUiState {
                                        copy(
                                            isLoading = false,
                                            errorMessage = backendResult.message
                                        )
                                    }
                                }
                                else -> {
                                    updateUiState { copy(isLoading = false) }
                                }
                            }
                        }
                    } else {
                        handleAuthError(error?.message ?: "Google Sign-In failed")
                    }
                }
            } catch (e: Exception) {
                handleAuthError(e.message ?: "Google Sign-In error")
            }
        }
    }

    // ==================== Helper Methods ====================

    private fun handleAuthError(message: String) {
        // Check if it's a user cancellation - normal and expected
        val isCancellation = isUserCancellation(message)

        if (isCancellation) {
            // User cancelled Google sign-in - reset to idle without showing error
            updateUiState {
                copy(
                    isLoading = false
                )
            }
        } else {
            // Real error - show to user
            updateUiState {
                copy(
                    isLoading = false,
                    errorMessage = message
                )
            }
        }
    }

    private fun isUserCancellation(message: String): Boolean {
        val lowerMessage = message.lowercase()
        return lowerMessage.contains("cancel") ||
               lowerMessage.contains("cancelled") ||
               lowerMessage.contains("cancellationexception") ||
               lowerMessage.contains("user cancelled")
    }

    // ==================== UI Event Handlers ====================

    fun onEmailChanged(email: String) {
        updateUiState { copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        updateUiState { copy(password = password) }
    }

    fun onRememberMeChanged(remembered: Boolean) {
        updateUiState { copy(rememberMe = remembered) }
    }

    fun clearError() {
        updateUiState { copy(errorMessage = null) }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}