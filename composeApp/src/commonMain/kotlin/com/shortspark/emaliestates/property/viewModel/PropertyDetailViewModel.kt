package com.shortspark.emaliestates.property.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyDetailViewModel(
    private val propertyId: String,
    private val propertySDK: PropertySDK
) : ViewModel() {

    private val _propertyState = MutableStateFlow<RequestState<Property?>>(RequestState.Idle)
    val propertyState: StateFlow<RequestState<Property?>> = _propertyState.asStateFlow()

    // Holds all properties for the "Similar properties" section
    private val _similarProperties = MutableStateFlow<List<Property>>(emptyList())
    val similarProperties: StateFlow<List<Property>> = _similarProperties.asStateFlow()

    init {
        loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            _propertyState.value = RequestState.Loading
            _propertyState.value = propertySDK.fetchPropertyById(propertyId)

            // Load similar properties (same type, excluding self)
            val allResult = propertySDK.getAllProperties()
            if (allResult is RequestState.Success) {
                val current = (_propertyState.value as? RequestState.Success)?.data
                _similarProperties.value = allResult.data
                    .filter { it.id != propertyId && it.type == current?.type }
                    .take(6)
            }
        }
    }

    fun scheduleTour() {
        // TODO: navigate to tour booking flow
    }

    fun refresh() = loadProperty()
}