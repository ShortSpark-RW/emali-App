package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.shortspark.emaliestates.domain.Category
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen1(navController: NavController) {
    val viewModel = koinViewModel<MainViewModel>()
    val propertiesState by viewModel.allProperties
    val categoriesState by viewModel.allCategories

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Categories horizontal scroll
        val catState = categoriesState
        if (catState is RequestState.Success) {
            if (catState.data.isNotEmpty()) {
                CategoryFilterChips(
                    categories = catState.data,
                    selectedCategoryId = (propertiesState as? RequestState.Success)?.data?.firstOrNull()?.let {
                        // Get selected category from viewModel if needed
                        null
                    },
                    onCategorySelected = { categoryId ->
                        viewModel.selectCategory(categoryId)
                    }
                )
            }
        }

        // Properties list
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (val state = propertiesState) {
                is RequestState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is RequestState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is RequestState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = "No properties available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(state.data) { property ->
                                PropertyCard(
                                    property = property,
                                    onClick = {
                                        navController.navigate("property/${property.id}")
                                    }
                                )
                            }
                        }
                    }
                }
                is RequestState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        CategoryChip(
            name = "All",
            isSelected = selectedCategoryId == null,
            onClick = { onCategorySelected(null) }
        )

        categories.forEach { category ->
            CategoryChip(
                name = category.name,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = name,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
               else MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
    )
}

@Composable
fun PropertyCard(
    property: Property,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$${property.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = property.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            property.locationId?.let {
                Text(
                    text = "Location: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${property.bedrooms} bed",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${property.bathrooms} bath",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${property.area} sq ft",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
