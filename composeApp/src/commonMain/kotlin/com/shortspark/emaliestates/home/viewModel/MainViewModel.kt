package com.shortspark.emaliestates.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Home screen (property listing).
 *
 * @param properties List of properties to display
 * @param isLoading True when initial load is in progress
 * @param isLoadingMore True when pagination load is in progress
 * @param error Error message if any, null otherwise
 * @param hasMore True if there are more pages to load (used for infinite scroll)
 * @param selectedCategoryId Currently selected category filter ID, null for all categories
 */
data class HomeUiState(
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
    val selectedCategoryId: String? = null
)

/**
 * ViewModel for the Home screen.
 * Handles property listing with category filtering and infinite scroll pagination.
 */
class MainViewModel(
    private val sdk: PropertySDK
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    companion object {
        private const val PAGE_SIZE = 20
    }

    private var currentPage = 1

    init {
        // Load initial properties (no filter)
        loadProperties(refresh = true)
    }

    /**
     * Loads properties, either initial load or refresh.
     * If refresh is true, resets pagination and clears existing list.
     *
     * @param categoryId Optional category ID to filter by
     */
    fun loadProperties(categoryId: String? = null, refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null, selectedCategoryId = categoryId) }
        } else {
            // Normal category change (non-refresh) - treat as new load
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null, selectedCategoryId = categoryId, properties = emptyList()) }
        }

        viewModelScope.launch {
            val result = sdk.getAllProperties(
                page = 1,
                limit = PAGE_SIZE,
                categoryId = categoryId
            )
            handleResult(result, isMoreLoad = false)
        }
    }

    /**
     * Loads the next page of properties (infinite scroll).
     * Called when user scrolls to the bottom of the list.
     */
    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoadingMore || !current.hasMore || current.isLoading) {
            return
        }

        currentPage++
        _uiState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            val result = sdk.getAllProperties(
                page = currentPage,
                limit = PAGE_SIZE,
                categoryId = current.selectedCategoryId
            )
            handleResult(result, isMoreLoad = true)
        }
    }

    private fun handleResult(result: RequestState<List<Property>>, isMoreLoad: Boolean) {
        when (result) {
            is RequestState.Success -> {
                val newProperties = result.data
                val hasMore = newProperties.size >= PAGE_SIZE

                if (isMoreLoad) {
                    // Append to existing list
                    val current = _uiState.value
                    _uiState.update {
                        it.copy(
                            properties = current.properties + newProperties,
                            isLoadingMore = false,
                            hasMore = hasMore
                        )
                    }
                } else {
                    // Initial load or refresh
                    _uiState.update {
                        it.copy(
                            properties = newProperties,
                            isLoading = false,
                            isLoadingMore = false,
                            hasMore = hasMore,
                            error = null
                        )
                    }
                }
            }
            is RequestState.Error -> {
                if (isMoreLoad) {
                    // For pagination errors, just stop loading more but keep existing data
                    _uiState.update { it.copy(isLoadingMore = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "An error occurred"
                        )
                    }
                }
            }
            else -> {
                // Loading or Idle - shouldn't happen here but handle gracefully
            }
        }
    }

    /**
     * Clears any error state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
