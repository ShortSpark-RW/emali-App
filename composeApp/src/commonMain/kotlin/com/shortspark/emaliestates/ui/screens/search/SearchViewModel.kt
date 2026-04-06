package com.shortspark.emaliestates.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val propertySDK: PropertySDK
) : ViewModel() {

    private val _properties = MutableStateFlow<RequestState<List<Property>>>(RequestState.Idle)
    val properties: StateFlow<RequestState<List<Property>>> = _properties.asStateFlow()

    private val _filteredProperties = MutableStateFlow<List<Property>>(emptyList())
    val filteredProperties: StateFlow<List<Property>> = _filteredProperties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Apply search filters and fetch properties
     */
    fun applyFilters(filters: FilterState) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Convert filters to API parameters
            val query = filters.query.ifBlank { null }
            val propertyType = if (filters.propertyType == "Any") null else filters.propertyType
            val minPrice = if (filters.minPrice > 0) filters.minPrice * 1_000_000.0 else null // Convert millions to actual
            val maxPrice = if (filters.maxPrice < 5) filters.maxPrice * 1_000_000.0 else null
            val minSurface = if (filters.minSurface > 0) filters.minSurface.toDouble() else null
            val maxSurface = if (filters.maxSurface < 500) filters.maxSurface.toDouble() else null
            val rooms = if (filters.rooms != "Any") filters.rooms.toIntOrNull() else null
            val bathrooms = if (filters.bathrooms != "Any") filters.bathrooms.toIntOrNull() else null
            val saleType = when (filters.tab) {
                ListingTab.FOR_SALE -> "FOR_SALE"
                ListingTab.FOR_RENT -> "FOR_RENT"
                ListingTab.RECENTLY_SOLD -> "SOLD"
            }

            val result = propertySDK.searchProperties(
                query = query,
                propertyType = propertyTypeToApiFormat(propertyType),
                minPrice = minPrice,
                maxPrice = maxPrice,
                minSurface = minSurface,
                maxSurface = maxSurface,
                rooms = rooms,
                bathrooms = bathrooms,
                saleType = saleType
            )

            _properties.value = result

            when (result) {
                is RequestState.Success -> {
                    _filteredProperties.value = result.data
                    if (result.data.isEmpty()) {
                        _error.value = null // Empty state handled separately
                    }
                }
                is RequestState.Error -> {
                    _error.value = result.message
                    _filteredProperties.value = emptyList()
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Simple text search without filters (for future use)
     */
    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = propertySDK.searchProperties(query = query)

            _properties.value = result
            when (result) {
                is RequestState.Success -> _filteredProperties.value = result.data
                is RequestState.Error -> {
                    _error.value = result.message
                    _filteredProperties.value = emptyList()
                }
                else -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Clear search results and reset state
     */
    fun clearSearch() {
        _properties.value = RequestState.Idle
        _filteredProperties.value = emptyList()
        _error.value = null
        _isLoading.value = false
    }

    private fun propertyTypeToApiFormat(type: String?): String? {
        return when (type) {
            "Single-family home" -> "HOUSE"
            "Apartment" -> "APARTMENT"
            "Duplex" -> "DUPLEX"
            "Villa" -> "VILLA"
            "Bungalow" -> "BUNGALOW"
            "Land plot" -> "LAND"
            else -> null
        }
    }
}
