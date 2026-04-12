package com.shortspark.emaliestates.home.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.CategorySDK
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias CachedProperties = MutableState<RequestState<List<Property>>>
typealias CachedCategories = MutableState<RequestState<List<Category>>>

class MainViewModel(
    private val sdk: PropertySDK,
    private val categorySDK: CategorySDK
) : ViewModel() {

    var allProperties: CachedProperties = mutableStateOf(RequestState.Idle)
        private set

    var allCategories: CachedCategories = mutableStateOf(RequestState.Idle)
        private set

    private val _selectedCategoryId = mutableStateOf<String?>(null)
    val selectedCategoryId: State<String?> = _selectedCategoryId

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.Default) {
            // Then trigger refresh
            loadCategories()
            loadProperties(_selectedCategoryId.value)
        }
    }

    fun loadProperties(categoryId: String? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            _selectedCategoryId.value = categoryId
            // Only show loading if we don't have data already (prevent flicker)
            if (allProperties.value !is RequestState.Success) {
                withContext(Dispatchers.Main) { allProperties.value = RequestState.Loading }
            }

            val result = if (categoryId != null) {
                val all = sdk.getAllProperties()
                if (all is RequestState.Success) {
                    RequestState.Success(all.data.filter { it.categoryId == categoryId })
                } else {
                    all
                }
            } else {
                sdk.getAllProperties()
            }
            withContext(Dispatchers.Main) {
                allProperties.value = result
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.Default) {
            // Only show loading if we don't have cached data
            if (allCategories.value !is RequestState.Success) {
                withContext(Dispatchers.Main) { allCategories.value = RequestState.Loading }
            }

            val result = categorySDK.getCategories()
            withContext(Dispatchers.Main) {
                allCategories.value = result
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        loadProperties(categoryId)
    }

    fun refresh() {
        loadCategories()
        loadProperties(_selectedCategoryId.value)
    }
}
