package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel handling OTP verification and resend functionality
 */
class OtpVerificationViewModel(
    private val sdk: AuthSDK
) : ViewModel() {

    // UI State for OTP verification
    data class OtpUiState(
        val email: String = "",
        val otp: String = "",
        val emailError: String? = null,
        val otpError: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val currentUser: User? = null
    )

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    // Convenience properties
    val email: String get() = _uiState.value.email
    val otp: String get() = _uiState.value.otp
    val emailError: String? get() = _uiState.value.emailError
    val otpError: String? get() = _uiState.value.otpError
    val isLoading: Boolean get() = _uiState.value.isLoading
    val errorMessage: String? get() = _uiState.value.errorMessage
    val currentUser: User? get() = _uiState.value.currentUser

    private fun updateUiState(block: OtpUiState.() -> OtpUiState) {
        _uiState.value = _uiState.value.block()
    }

    // ==================== OTP Verification ====================

    /**
     * Verify OTP after signup or forgot password
     */
    fun verifyOtp(email: String, otp: String) {
        updateUiState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.verifyOtp(email, otp)
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

    /**
     * Resend OTP verification code
     */
    fun resendOtp(email: String) {
        updateUiState { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = sdk.resendOtp(email)
            when (result) {
                is RequestState.Success<*> -> {
                    updateUiState {
                        copy(
                            isLoading = false
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

    // ==================== UI Event Handlers ====================

    fun onEmailChanged(email: String) {
        updateUiState { copy(email = email) }
    }

    fun onOtpChanged(otp: String) {
        updateUiState { copy(otp = otp) }
    }

    fun clearError() {
        updateUiState { copy(errorMessage = null) }
    }

    fun resetState() {
        _uiState.value = OtpUiState()
    }
}