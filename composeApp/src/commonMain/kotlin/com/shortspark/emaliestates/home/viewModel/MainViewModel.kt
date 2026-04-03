package com.shortspark.emaliestates.home.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.PropertySDK
import com.shortspark.emaliestates.data.repository.CategoryRepository
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias CachedProperties = MutableState<RequestState<List<Property>>>
typealias CachedCategories = MutableState<RequestState<List<Category>>>

class MainViewModel(
    private val sdk: PropertySDK,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    var allProperties: CachedProperties = mutableStateOf(RequestState.Idle)
        private set

    var allCategories: CachedCategories = mutableStateOf(RequestState.Idle)
        private set

    private var selectedCategoryId: String? = null

    init {
        loadCategories()
        loadProperties()
    }

    fun loadProperties(categoryId: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedCategoryId = categoryId
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
        viewModelScope.launch(Dispatchers.IO) {
            val result = categoryRepository.getCategories()
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
        loadProperties(selectedCategoryId)
    }
}
