package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoogleAuthUiState (
    var errorMessage: String? = null,
    var isLoading: Boolean = false,
    var user: String? = null,
)

class GoogleAuthViewModel(
    var googleAuthManager: GoogleAuthManager
): ViewModel() {

    private val _googleAuthUiState = MutableStateFlow(GoogleAuthUiState())
    val googleAuthUiState = _googleAuthUiState.asStateFlow()


    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                googleAuthManager.signIn { user, error ->
                    if (error == null && user != null) {
                        println("User: $user")
                        val user = user.name
                        _googleAuthUiState.update {
                            it.copy(
                                user = user,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    } else {
                        error?.printStackTrace()
                        _googleAuthUiState.update {
                            it.copy(
                                user = null,
                                isLoading = false,
                                errorMessage = error?.message
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _googleAuthUiState.update {
                    it.copy(
                        user = null,
                        isLoading = false,
                        errorMessage = e.message
                    )
                }

            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthManager.signOut(null)
            _googleAuthUiState.update {
                it.copy(
                    user = null,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

}