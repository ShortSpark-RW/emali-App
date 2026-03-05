package com.shortspark.emaliestates.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val propertySDK: PropertySDK
) : ViewModel() {

    private val _propertiesState = MutableStateFlow<RequestState<List<Property>>>(RequestState.Idle)
    val propertiesState: StateFlow<RequestState<List<Property>>> = _propertiesState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _propertiesState.value = RequestState.Loading
            _propertiesState.value = propertySDK.getAllProperties()
        }
    }

    fun refresh() = loadProperties()
}
