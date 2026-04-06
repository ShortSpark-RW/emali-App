package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.navigation.BaseScreen
import org.koin.compose.viewmodel.koinViewModel

// ─── UI Data Models ──────────────────────────────────────────────────────────

data class PropertyUiModel(
    val id: String,
    val title: String,
    val location: String?,
    val price: String,
    val rating: Float,
    val badge: String,
    val imageUrl: String?,
    val isFavorited: Boolean = false,
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: String? = null
)

data class CategoryUiModel(
    val id: String,
    val name: String
)

// ─── HomeScreen ──────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    viewModel: MainViewModel = koinViewModel(),
    onPropertyClick: (String) -> Unit = {},
    onSeeAllClick: (String) -> Unit = {},
    navController: androidx.navigation.NavController = androidx.navigation.compose.rememberNavController()
) {
    val propertiesState by viewModel.allProperties
    val categoriesState by viewModel.allCategories

    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var selectedCategoryId: String? by remember { mutableStateOf(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = (categoriesState as? RequestState.Success)?.data?.let { cats ->
        listOf(CategoryUiModel(id = "all", name = "All")) + cats.map { cat ->
            CategoryUiModel(id = cat.id, name = cat.name)
        }
    } ?: listOf(CategoryUiModel("all", "All"))

    val properties = (propertiesState as? RequestState.Success)?.data ?: emptyList()
    val isLoading = propertiesState is RequestState.Loading
    val error = (propertiesState as? RequestState.Error)?.message

    // Group properties by type
    val propertyGroups = remember(properties, selectedCategoryId) {
        groupPropertiesByType(properties, selectedCategoryId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Bottom bar is handled by MainScreenContainer
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            HomeTopBar()

            Spacer(Modifier.height(16.dp))

            // Search bar with filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = {
                        // Pass current query to filter screen
                        navController.currentBackStackEntry?.savedStateHandle?.set("searchQuery", searchQuery)
                        navController.navigate(BaseScreen.Search.route)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Category chips
            if (categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, category ->
                        val isSelected = when {
                            index == 0 -> selectedCategoryId == null
                            else -> category.id == selectedCategoryId
                        }

                        CategoryChip(
                            label = category.name,
                            selected = isSelected,
                            onClick = {
                                selectedCategoryIndex = index
                                selectedCategoryId = if (index == 0) null else category.id
                                viewModel.selectCategory(selectedCategoryId)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // Property sections
            if (isLoading) {
                LoadingState()
            } else if (error != null) {
                ErrorState(message = error, onRetry = { viewModel.refresh() })
            } else {
                propertyGroups.forEach { (type, props) ->
                    if (props.isNotEmpty()) {
                        PropertySection(
                            title = getSectionTitle(type),
                            properties = props.map { it.toUiModel() },
                            onSeeAll = { onSeeAllClick(type) },
                            onPropertyClick = onPropertyClick
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }

                if (propertyGroups.all { it.value.isEmpty() }) {
                    EmptyState(message = "No properties found")
                }
            }

            Spacer(Modifier.height(80.dp)) // Space for bottom nav
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "e",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            Text(
                text = "MALI ESTATES",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )
        }

        Spacer(Modifier.weight(1f))

        // Notification icon
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        // Message icon
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Messages",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp)),
        placeholder = {
            Text("Search properties...", color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Tune, // Equalizer/filter icon
                contentDescription = "Filter search",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onFilterClick)
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

// ─── Filter Button ────────────────────────────────────────────────────────────

@Composable
private fun FilterButton() {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {},
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ─── Category Chip ────────────────────────────────────────────────────────────

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─── Property Section ─────────────────────────────────────────────────────────

@Composable
private fun PropertySection(
    title: String,
    properties: List<PropertyUiModel>,
    onSeeAll: () -> Unit,
    onPropertyClick: (String) -> Unit
) {
    Column {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See all",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSeeAll() }
            )
        }

        Spacer(Modifier.height(14.dp))

        // Horizontal scrollable cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            properties.forEach { property ->
                PropertyCard(
                    property = property,
                    onClick = { onPropertyClick(property.id) }
                )
            }
        }
    }
}

// ─── Property Card ────────────────────────────────────────────────────────────

@Composable
fun PropertyCard(
    property: PropertyUiModel,
    onClick: () -> Unit
) {
    var favorited by remember { mutableStateOf(property.isFavorited) }

    Column(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        // Image container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (property.imageUrl != null) {
                AsyncImage(
                    model = property.imageUrl,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC000000))
                        )
                    )
            )

            // Badge - bottom left
            property.badge.takeIf { it.isNotBlank() }?.let { badge ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x99000000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Favorite button - top right
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
                    .clickable { favorited = !favorited },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (favorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (favorited) MaterialTheme.colorScheme.secondary else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Card body
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Title
            Text(
                text = property.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // Rating + Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = property.rating.toString(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                // Price
                Text(
                    text = property.price,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            // Location + specs row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = property.location ?: "Location unknown",
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick specs (bed/bath/area)
                if (property.bedrooms > 0 || property.bathrooms > 0 || property.area != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (property.bedrooms > 0) {
                            Text(
                                text = "${property.bedrooms} bed",
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                fontSize = 10.sp
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        if (property.bathrooms > 0) {
                            Text(
                                text = "${property.bathrooms} bath",
                                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── State UI Components ──────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading properties",
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
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
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            fontSize = 14.sp
        )
    }
}

// ─── Mapping & Utilities ──────────────────────────────────────────────────────

private fun Property.toUiModel(): PropertyUiModel {
    val formattedPrice = when {
        price < 1_000_000 -> "$${price.toInt()}"
        else -> "$${(price / 1_000_000).toInt()}M"
    }

    val locationName = locationId ?: "Unknown location"

    return PropertyUiModel(
        id = id,
        title = title,
        location = locationName,
        price = formattedPrice,
        rating = 0f, // TODO: Add rating to domain.Property
        badge = type.ifBlank { "Property" },
        imageUrl = featuredImg,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        area = if (area > 0) "${area.toInt()} m²" else null
    )
}

private fun groupPropertiesByType(
    properties: List<Property>,
    categoryId: String?
): Map<String, List<Property>> {
    val filtered = if (categoryId != null) {
        properties.filter { it.categoryId == categoryId }
    } else {
        properties
    }

    return filtered.groupBy { it.saleType.ifBlank { "other" } }
}

private fun getSectionTitle(type: String): String = when (type.lowercase()) {
    "rent" -> "Houses for Rent"
    "sale" -> "Houses for Sale"
    "land" -> "Land for Sale"
    else -> "Properties"
}
