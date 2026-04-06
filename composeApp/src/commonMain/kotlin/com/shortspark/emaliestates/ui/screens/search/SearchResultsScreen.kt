package com.shortspark.emaliestates.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.WindowInsets
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.home.presentation.PropertyCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchResultsScreen(
    navController: NavController = rememberNavController(),
    viewModel: SearchViewModel = koinViewModel()
) {
    // Retrieve filters from back stack entry
    val filters = navController.previousBackStackEntry?.savedStateHandle?.get<FilterState>("searchFilters")

    val properties by viewModel.filteredProperties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Apply filters when screen is shown
    LaunchedEffect(filters) {
        filters?.let { viewModel.applyFilters(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Top bar
        SearchResultsTopBar(
            onBack = { navController.popBackStack() },
            resultCount = properties.size
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }

            error != null -> {
                ErrorState(
                    message = error!!,
                    onRetry = { filters?.let { viewModel.applyFilters(it) } }
                )
            }

            properties.isEmpty() -> {
                EmptyState(
                    message = "No properties found matching your criteria"
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(properties) { property ->
                        PropertyCard(
                            property = property.toUiModel(),
                            onClick = { navController.navigate("properties/${property.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsTopBar(
    onBack: () -> Unit,
    resultCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "$resultCount Properties Found",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Error loading properties",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Extension to convert Property domain model to UI model
private fun Property.toUiModel(): com.shortspark.emaliestates.home.presentation.PropertyUiModel {
    return com.shortspark.emaliestates.home.presentation.PropertyUiModel(
        id = id,
        title = title,
        location = placeName ?: locationId ?: "Unknown location",
        price = formatPrice(price),
        rating = 0f,
        badge = type.name,
        imageUrl = featuredImg,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        area = if (area > 0) "${area.toInt()} m²" else null
    )
}

private fun formatPrice(price: Float): String {
    return when {
        price < 1_000_000 -> "$${price.toInt()}"
        else -> "$${(price / 1_000_000).toInt()}M"
    }
}
