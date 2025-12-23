package com.shortspark.emaliestates.auth.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.AuthSDK
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.domain.auth.User
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sdk: AuthSDK
) : ViewModel() {

    var loginState = mutableStateOf<RequestState<User>>(RequestState.Idle)
        private set

    fun login(email: String, password: String) {
        loginState.value = RequestState.Loading
        viewModelScope.launch {
            // Remove the unsafe cast and handle types within the 'when' block
            loginState.value = when (val result = sdk.login(email, password)) {
                is RequestState.Success -> {
                    // Safely cast 'result.data' to 'User' only in the success case
                    RequestState.Success(result.data as User)
                }
                is RequestState.Error -> {
                    // The error state is correctly inferred without any casting
                    RequestState.Error(result.message)
                }
                // These states are already correctly typed
                is RequestState.Loading -> RequestState.Loading
                is RequestState.Idle -> RequestState.Idle
            }
        }
    }
}