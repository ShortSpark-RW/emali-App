package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.RequestState
import com.shortspark.emaliestates.navigation.BaseScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

// ─── Screen Entry Point ───────────────────────────────────────────────────────

@Composable
fun HomeScreen1(navController: NavController) {
    val viewModel = koinViewModel<MainViewModel>()
    val propertiesState by viewModel.allProperties

    HomeContent(propertiesState = propertiesState, navController= navController)
}

// ─── Content ──────────────────────────────────────────────────────────────────

@Composable
@Preview
fun HomeContent(
    propertiesState: RequestState<List<Property>> = RequestState.Idle,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        HomeTopBar()
        Spacer(Modifier.height(16.dp))
        HomeSearchBar(onFilterClick = {})
        Spacer(Modifier.height(16.dp))
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(Modifier.height(20.dp))

        when (propertiesState) {
            is RequestState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }

            is RequestState.Error -> {
                Text(
                    text = propertiesState.message.toString(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )
            }

            is RequestState.Success -> {
                val all = propertiesState.data

                // Filter list by selected category tab
                val filtered = if (selectedCategory == "All") all
                else all.filter {
                    it.type.equals(selectedCategory, ignoreCase = true) ||
                            it.saleType.equals(selectedCategory, ignoreCase = true)
                }

                val forRent     = filtered.filter { it.saleType.equals("rent", ignoreCase = true) }
                val forSale     = filtered.filter { it.saleType.equals("sale", ignoreCase = true) }
                val landForSale = filtered.filter { it.type.equals("land", ignoreCase = true) }

                if (forRent.isNotEmpty()) {
                    PropertySection(
                        title = "House for Rent",
                        properties = forRent,
                        onSeeAll = {},
                        onCardClick = {
                            id -> navController.navigate(BaseScreen.PropertyDetail.createRoute(id))
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                }

                if (forSale.isNotEmpty()) {
                    PropertySection(
                        title = "House for Sale",
                        properties = forSale,
                        onSeeAll = {},
                        onCardClick = { id -> navController.navigate(BaseScreen.PropertyDetail.createRoute(id)) }
                    )
                    Spacer(Modifier.height(20.dp))
                }

                if (landForSale.isNotEmpty()) {
                    PropertySection(
                        title = "Land for Sale",
                        properties = landForSale,
                        onSeeAll = {},
                        onCardClick = { id -> navController.navigate(BaseScreen.PropertyDetail.createRoute(id)) }
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            else -> Unit
        }

        Spacer(Modifier.height(80.dp)) // bottom nav clearance
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppBrandLogo()
        TopBarActions()
    }
}

@Composable
fun AppBrandLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "e",
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "MALI ESTATES",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun TopBarActions() {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TopBarIconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(22.dp)
            )
        }
        TopBarIconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Outlined.Chat,
                contentDescription = "Messages",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun TopBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@Composable
fun HomeSearchBar(onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchTextField(modifier = Modifier.weight(1f))
        FilterButton(onClick = onFilterClick)
    }
}

@Composable
fun SearchTextField(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🔍", fontSize = 14.sp)
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            text = "search",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                    inner()
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun FilterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "⚙", fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

// ─── Category Tabs ────────────────────────────────────────────────────────────

val propertyCategories = listOf("All", "Apartment", "Villa", "Bungalow", "Duplex", "Office", "Land")

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        propertyCategories.forEach { category ->
            CategoryChip(
                label = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor    = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
    val textColor  = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// ─── Property Section ─────────────────────────────────────────────────────────

@Composable
fun PropertySection(
    title: String,
    properties: List<Property>,
    onSeeAll: () -> Unit,
    onCardClick: (String) -> Unit = {}
) {
    Column {
        PropertySectionHeader(title = title, onSeeAll = onSeeAll)
        Spacer(Modifier.height(12.dp))
        PropertyCardRow(properties = properties, onCardClick = onCardClick)
    }
}

@Composable
fun PropertySectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Text(
            text = "See all",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun PropertyCardRow(
    properties: List<Property>,
    onCardClick: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        properties.forEach { property ->
            PropertyCard(
                property = property,
                onCardClick = onCardClick
            )
        }
    }
}

// ─── Property Card ────────────────────────────────────────────────────────────

@Composable
fun PropertyCard(
    property: Property,
    onFavoriteToggle: (String) -> Unit = {},
    onCardClick: (String) -> Unit = {}
) {
    var isFav by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCardClick(property.id) }
    ) {
        // ── Image area ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (!property.featuredImg.isNullOrBlank()) {
                AsyncImage(
                    model = property.featuredImg,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Verified badge — top start
            if (property.isVerified) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "✓ Verified", color = Color.White, fontSize = 9.sp)
                }
            }

            // Favourite button — top end (stopPropagation so it doesn't trigger card click)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFav) MaterialTheme.colorScheme.secondary
                        else Color.Black.copy(alpha = 0.35f)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isFav = !isFav
                        onFavoriteToggle(property.id)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (isFav) "♥" else "♡", fontSize = 13.sp, color = Color.White)
            }

            // Type tag — bottom start
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = property.type.replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        // ── Details ──
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = property.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1
            )

            Spacer(Modifier.height(5.dp))

            // Beds / Baths / Area chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PropertySpecChip(icon = "🛏", value = "${property.bedrooms}")
                PropertySpecChip(icon = "🚿", value = "${property.bathrooms}")
                PropertySpecChip(icon = "📐", value = "${property.area.toInt()}m²")
            }

            Spacer(Modifier.height(6.dp))

            // Price
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${formatPrice(property.price)}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (property.saleType.equals("rent", ignoreCase = true)) {
                    Text(
                        text = "/mo",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ─── Spec Chip ────────────────────────────────────────────────────────────────

@Composable
fun PropertySpecChip(icon: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = icon, fontSize = 10.sp)
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 10.sp
        )
    }
}

// ─── Utility ──────────────────────────────────────────────────────────────────

private fun formatPrice(price: Float): String = when {
    price >= 1_000_000 -> "${(price / 1_000_000 * 10).toInt() / 10.0}M"
    price >= 1_000     -> "${(price / 1_000).toInt()}K"
    else               -> "${price.toInt()}"
}