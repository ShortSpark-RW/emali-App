package com.shortspark.emaliestates.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface SplashEvent {
    data object Authenticated : SplashEvent
    data object Unauthenticated : SplashEvent
}

class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<SplashEvent>()
    val events = _events.asSharedFlow()

    fun checkAuthState() {
        viewModelScope.launch {
            // Small delay to show splash at least briefly
            delay(500)
            if (authRepository.getToken() != null) {
                _events.emit(SplashEvent.Authenticated)
            } else {
                _events.emit(SplashEvent.Unauthenticated)
            }
        }
    }
}
