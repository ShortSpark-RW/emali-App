package com.shortspark.emaliestates.home.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias CachedProperties = MutableState<RequestState<List<Property>>>
class MainViewModel(
    private val sdk: PropertySDK
): ViewModel() {
    var allProperties: CachedProperties = mutableStateOf(RequestState.Idle)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = sdk.getAllProperties()
            withContext(Dispatchers.Main) {
                allProperties.value = result
            }
        }
    }
}