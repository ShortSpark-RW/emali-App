package com.shortspark.emaliestates.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunildhiman90.kmauth.supabase.SupabaseAuthManager
import com.sunildhiman90.kmauth.supabase.model.SupabaseOAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FacebookAuthUiState (
    var errorMessage: String? = null,
    var isLoading: Boolean = false,
    var user: String? = null,
)

class FacebookAuthViewModel(
    var facebookAuthManager: SupabaseAuthManager
): ViewModel() {

    private val _facebookAuthUiState = MutableStateFlow(FacebookAuthUiState())
    val facebookAuthUiState = _facebookAuthUiState.asStateFlow()


    fun signInWithFacebook() {
        viewModelScope.launch {
            try {
                val result = facebookAuthManager.signInWith(
                    supabaseOAuthProvider = SupabaseOAuthProvider.FACEBOOK,
                )

                val user = result.getOrNull()
                val error = result.exceptionOrNull()

                println("User: $user")
                println("Error: $error")

                if (error == null && user != null) {
                    println("User: $user")
                    val user = user.name
                    _facebookAuthUiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    error?.printStackTrace()
                    _facebookAuthUiState.update {
                        it.copy(
                            user = null,
                            isLoading = false,
                            errorMessage = error?.message
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _facebookAuthUiState.update {
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
            facebookAuthManager.signOut()
            _facebookAuthUiState.update {
                it.copy(
                    user = null,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

}