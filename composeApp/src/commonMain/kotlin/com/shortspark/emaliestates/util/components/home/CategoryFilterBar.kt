package com.shortspark.emaliestates.util.components.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.home.viewModel.CategoryViewModel

/**
 * A horizontally scrollable filter bar for categories.
 *
 * @param categories List of all available categories
 * @param selectedCategoryId Currently selected category ID, null for "All"
 * @param onCategorySelected Callback when a category is selected
 * @param showAllOption If true, adds an "All" option at the start
 */
@Composable
fun CategoryFilterBar(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    showAllOption: Boolean = true,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" option
        if (showAllOption) {
            CategoryChip(
                category = Category(
                    id = "",
                    name = "All",
                    description = null,
                    icon = null,
                    color = null,
                    order = 0,
                    isActive = true
                ),
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
        }

        // Category chips
        categories.forEach { category ->
            CategoryChip(
                category = category,
                isSelected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) }
            )
        }

        // Add spacing at end for scroll padding
        Spacer(modifier = Modifier.padding(end = 8.dp))
    }
}

/**
 * Convenience composable that fetches categories from ViewModel and renders the filter bar.
 * This combines ViewModel state with UI.
 *
 * @param viewModel CategoryViewModel providing categories and selection state
 * @param onCategorySelected Callback when category changes
 */
@Composable
fun CategoryFilterBar(
    viewModel: CategoryViewModel,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()

    when (val state = categoriesState) {
        is RequestState.Success -> {
            CategoryFilterBar(
                categories = state.data,
                selectedCategoryId = viewModel.selectedCategory.value,
                onCategorySelected = onCategorySelected,
                modifier = modifier
            )
        }
        else -> {
            // Show loading or error state as needed; could be empty
            // For now, we'll just not show anything while loading
        }
    }
}
