package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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
        // Categories horizontal scroll with better styling
        val catState = categoriesState
        if (catState is RequestState.Success) {
            if (catState.data.isNotEmpty()) {
                CategoryFilterChips(
                    categories = catState.data,
                    selectedCategoryId = (propertiesState as? RequestState.Success)?.data?.firstOrNull()?.categoryId,
                    onCategorySelected = { categoryId ->
                        viewModel.selectCategory(categoryId)
                    }
                )
            }
        }

        // Properties list with optimized lazy loading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val state = propertiesState) {
                is RequestState.Idle -> {
                    // Show skeleton loading for initial state
                    PropertiesList(
                        properties = emptyList(),
                        navController = navController,
                        showSkeletons = true
                    )
                }
                is RequestState.Loading -> {
                    // When switching categories, keep showing old data if available
                    val previousSuccess = propertiesState.takeIf { it is RequestState.Success<*> } as? RequestState.Success<*>
                    val previousData = previousSuccess?.data as? List<Property>

                    if (previousData != null && previousData.isNotEmpty()) {
                        // Show existing data while loading new category
                        PropertiesList(
                            properties = previousData,
                            navController = navController,
                            isLoadingMore = true
                        )
                    } else {
                        // Show skeleton loading for initial load
                        PropertiesList(
                            properties = emptyList(),
                            navController = navController,
                            showSkeletons = true
                        )
                    }
                }
                is RequestState.Success<*> -> {
                    val data = state.data as? List<*>
                    if (data.isNullOrEmpty()) {
                        EmptyState(
                            onRetry = { viewModel.refresh() },
                            hasCategories = (categoriesState as? RequestState.Success)?.data?.isNotEmpty() == true
                        )
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        PropertiesList(
                            properties = data as List<Property>,
                            navController = navController,
                            isLoadingMore = false
                        )
                    }
                }
                is RequestState.Error -> {
                    ErrorState(
                        message = state.message ?: "An error occurred",
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(onRetry: () -> Unit, hasCategories: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration or icon
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (hasCategories) "No properties found" else "No properties available",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasCategories) {
                "Try selecting a different category"
            } else {
                "We're working on adding more properties. Please check back later."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text("Refresh")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun PropertiesList(
    properties: List<Property>,
    navController: NavController,
    listState: androidx.compose.foundation.lazy.LazyListState = rememberLazyListState(),
    isLoadingMore: Boolean = false,
    showSkeletons: Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        state = listState
    ) {
        val itemCount = if (showSkeletons) 10 else properties.size

        items(
            count = itemCount,
            key = { if (showSkeletons) it else properties[it].id }
        ) { index ->
            if (showSkeletons) {
                SkeletonPropertyCard()
            } else {
                val property = properties[index]
                PropertyCard(
                    property = property,
                    onClick = {
                        navController.navigate("property/${property.id}")
                    }
                )
            }
        }

        // Show loading indicator at bottom if loading more
        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun SkeletonPropertyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(0.dp)
        ) {
            // Skeleton image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            MaterialTheme.shapes.small
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description skeleton
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(16.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                MaterialTheme.shapes.small
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Specs row skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(width = 40.dp, height = 20.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    MaterialTheme.shapes.small
                                )
                        )
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = name,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() }
            .semantics {
                selected = isSelected
                contentDescription = "Category filter: $name${if (isSelected) ", selected" else ""}"
            },
        color = contentColor,
        fontSize = 14.sp,
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
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(0.dp)
        ) {
            // Property image with semantic description
            val imageUrl = property.featuredImg
            val imageContentDescription = "Property image: ${property.title}"

            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = imageContentDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder when no image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No Image",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title and price row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        // Limit title to 2 lines
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$${property.price}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Property description
                if (!property.description.isNullOrBlank()) {
                    Text(
                        text = property.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Property specs row with accessibility
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PropertySpec(
                        label = "${property.bedrooms}",
                        subLabel = "bed"
                    )
                    PropertySpec(
                        label = "${property.bathrooms}",
                        subLabel = "bath"
                    )
                    PropertySpec(
                        label = "${property.area}",
                        subLabel = "sq ft"
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertySpec(
    label: String,
    subLabel: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
