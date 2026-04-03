package com.shortspark.emaliestates.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shortspark.emaliestates.data.repository.CategoryRepository
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing category-related UI state.
 * Exposes categories list, selected category filter, and loading/error states.
 */
class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<RequestState<List<Category>>>(RequestState.Loading)
    val categoriesState: StateFlow<RequestState<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null) // null means "All"
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * Loads all categories from the repository (with caching).
     * Updates categoriesState with the result.
     */
    fun loadCategories(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _categoriesState.value = RequestState.Loading
            _categoriesState.value = categoryRepository.getCategories(forceRefresh)
        }
    }

    /**
     * Selects a category to filter properties.
     * Pass null to show all categories.
     */
    fun selectCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
    }

    /**
     * Clears the category filter, showing all properties.
     */
    fun clearCategoryFilter() {
        _selectedCategory.value = null
    }

    /**
     * Returns the currently selected category object from the list.
     */
    fun getSelectedCategory(categories: List<Category>): Category? {
        return categories.find { it.id == _selectedCategory.value }
    }
}
