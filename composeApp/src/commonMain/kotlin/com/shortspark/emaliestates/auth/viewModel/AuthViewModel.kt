package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val googleAuthManager: GoogleAuthManager
) : ViewModel() {

    // Unified State: Tracks loading, success (with User), or error for ANY login method
    private val _loginState = MutableStateFlow<RequestState<User>>(RequestState.Idle)
    val loginState = _loginState.asStateFlow()

    // --- Standard Email/Password Login ---
    fun login(email: String, password: String) {
        _loginState.update { RequestState.Loading }

        viewModelScope.launch {
            val result = sdk.login(email, password)
            updateState(result as RequestState<User>)
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
                        // 2. If Google sign-in succeeds, send the ID Token to your backend
                        viewModelScope.launch {
                            // Assuming sdk.google() returns RequestState<User> just like sdk.login()
                            // If sdk.google returns Unit, you'll need to fetch the profile manually after.
                            val backendResult = sdk.google(googleUser.idToken ?: "")
                            updateState(backendResult as RequestState<User>)
                        }
                    } else {
                        val msg = error?.message ?: "Google Sign-In canceled or failed"
                        _loginState.update { RequestState.Error(msg) }
                    }
                }
            } catch (e: Exception) {
                _loginState.update { RequestState.Error(e.message ?: "Unknown Google Auth Error") }
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

            // 3. Reset State to Idle
            _loginState.update { RequestState.Idle }
        }
    }

    // Helper to reduce code duplication
    private fun updateState(result: RequestState<User>) {
        _loginState.update {
            when (result) {
                is RequestState.Success -> RequestState.Success(result.data)
                is RequestState.Error -> RequestState.Error(result.message)
                is RequestState.Loading -> RequestState.Loading
                is RequestState.Idle -> RequestState.Idle
            }
        }
    }
}