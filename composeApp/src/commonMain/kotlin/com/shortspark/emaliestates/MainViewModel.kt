package com.shortspark.emaliestates

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.launch

typealias CachedProperties = MutableState<RequestState<List<Property>>>
class MainViewModel(
    private val sdk: PropertySDK
): ViewModel() {
    var allProperties: CachedProperties = mutableStateOf(RequestState.Idle)
        private set

    init {
        viewModelScope.launch {
            allProperties.value = sdk.getAllProperties()
        }
    }
}