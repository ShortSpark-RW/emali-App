package com.shortspark.emaliestates.auth.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.auth.viewModel.AuthOperation
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.launch

class VerifyOtpViewModel(
    private val sdk: AuthSDK,
    val email: String
) : ViewModel(), KoinComponent {

    private val authViewModel: AuthViewModel by inject()

    var otp by mutableStateOf("")
        private set
    var otpError by mutableStateOf<String?>(null)
        private set

    private val _verifyState = mutableStateOf<RequestState<Any>>(RequestState.Idle)
    val verifyState = _verifyState

    fun updateOtp(newOtp: String) {
        otp = newOtp
        otpError = null
    }

    fun validateOtp(): Boolean {
        return if (otp.length != 6) {
            otpError = "Please enter a valid 6-digit code"
            false
        } else {
            true
        }
    }

    fun verifyOtp(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        if (!validateOtp()) {
            return
        }

        // Set global auth state
        authViewModel.setLoading(true)
        authViewModel.setOperation(AuthOperation.VerifyOtp)
        authViewModel.setAuthError(null)

        _verifyState.value = RequestState.Loading

        viewModelScope.launch {
            val result = sdk.verifyOtp(
                email = email,
                otp = otp.trim()
            )

            // Reset global auth state
            authViewModel.setLoading(false)
            authViewModel.setOperation(AuthOperation.Idle)

            _verifyState.value = result

            when (result) {
                is RequestState.Success -> {
                    // Set current user in AuthViewModel for unified state management
                    val user = result.data as? com.shortspark.emaliestates.domain.auth.User
                    authViewModel.setCurrentUser(user)
                    onSuccess()
                }
                is RequestState.Error -> {
                    val errorMsg = result.message ?: "Verification failed"
                    authViewModel.setAuthError(errorMsg)
                    onError(errorMsg)
                }
                else -> {}
            }
        }
    }

    fun resetState() {
        _verifyState.value = RequestState.Idle
    }

    fun clearOtp() {
        otp = ""
        otpError = null
        resetState()
    }
}
