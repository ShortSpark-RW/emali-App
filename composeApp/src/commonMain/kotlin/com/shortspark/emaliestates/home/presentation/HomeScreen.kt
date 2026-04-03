package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.home.viewModel.CategoryViewModel
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.util.components.home.CategoryFilterBar
import com.shortspark.emaliestates.util.components.home.PropertyCard

/**
 * Home screen displaying property listings with category filtering and infinite scroll.
 */
@Composable
fun HomeScreen(
    navController: androidx.navigation.NavController
) {
    val mainViewModel: MainViewModel = koinViewModel()
    val categoryViewModel: CategoryViewModel = koinViewModel()

    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val categoriesState by categoryViewModel.categoriesState.collectAsStateWithLifecycle()

    val gridState = rememberLazyGridState()

    // Detect scroll to end for pagination
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null &&
                    lastVisibleItemIndex >= uiState.properties.size - 5 &&
                    !uiState.isLoadingMore &&
                    uiState.hasMore
                ) {
                    mainViewModel.loadNextPage()
                }
            }
    }

    // Pull-to-refresh
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = { mainViewModel.loadProperties(refresh = true) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar Placeholder
            SearchBarPlaceholder(modifier = Modifier.fillMaxWidth())

            // Category Filter Bar
            when (val state = categoriesState) {
                is RequestState.Success -> {
                    CategoryFilterBar(
                        viewModel = categoryViewModel,
                        onCategorySelected = { categoryId ->
                            // Update category selection in ViewModels
                            categoryViewModel.selectCategory(categoryId)
                            mainViewModel.loadProperties(categoryId = categoryId, refresh = true)
                        }
                    )
                }
                else -> {
                    // Could show loading placeholder or nothing
                }
            }

            // Property List Content
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.error != null && uiState.properties.isEmpty() -> {
                        ErrorView(
                            message = uiState.error ?: "Unknown error",
                            onRetry = { mainViewModel.loadProperties(categoryId = uiState.selectedCategoryId) }
                        )
                    }
                    uiState.properties.isEmpty() && uiState.isLoading -> {
                        LoadingView()
                    }
                    uiState.properties.isEmpty() -> {
                        EmptyView(message = "No properties found")
                    }
                    else -> {
                        // Build category map for quick lookup
                        val categoryMap = when (val state = categoriesState) {
                            is RequestState.Success -> {
                                state.data.associateBy { it.id }
                            }
                            else -> emptyMap()
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 300.dp),
                            state = gridState,
                            contentPadding = PaddingValues(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.properties.size) { index ->
                                val property = uiState.properties[index]
                                val categoryName = categoryMap[property.categoryId]?.name
                                PropertyCard(
                                    property = property,
                                    onClick = {
                                        // Navigate to property detail
                                        navController.navigate(BaseScreen.PropertyDetail.createRoute(property.id))
                                    },
                                    onFavoriteClick = { /* TODO: Implement favorite toggle */ },
                                    isFavorited = false, // TODO: get from favorite repository
                                    categoryName = categoryName
                                )
                            }

                            // Loading indicator for pagination
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBarPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Search properties...",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        TextButton(
            onClick = onRetry
        ) {
            Text(
                text = "Retry",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyView(message: String = "No properties available") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
