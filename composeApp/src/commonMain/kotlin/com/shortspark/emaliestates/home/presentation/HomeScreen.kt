package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.shortspark.emaliestates.domain.Property
import com.shortspark.emaliestates.domain.SaleType
import com.shortspark.emaliestates.home.viewModel.MainViewModel
import com.shortspark.emaliestates.navigation.Screen
import com.shortspark.emaliestates.util.helpers.formatPrice
import org.koin.compose.viewmodel.koinViewModel

// ─── UI Models ──────────────────────────────────────────────────────────────

data class PropertyUiModel(
    val id: String,
    val title: String,
    val location: String?,
    val price: String,
    val priceUnit: String? = null,
    val badge: String,
    val imageUrl: String?,
    val rating: Float = 4.5f
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
    val selectedCategoryId by viewModel.selectedCategoryId

    var searchQuery by remember { mutableStateOf("") }

    val categories = remember(categoriesState) {
        val cats = categoriesState.getDataOrNull()?.map { CategoryUiModel(it.id, it.name) } ?: emptyList()
        listOf(CategoryUiModel("all", "All")) + cats
    }

    val propertyGroups = remember(propertiesState) {
        val allProperties = propertiesState.getDataOrNull() ?: emptyList()
        
        val groupedByBadge = allProperties
            .map { it.toUiModel() }
            .groupBy { it.badge }

        val forRent = allProperties
            .filter { it.saleType == SaleType.RENT }
            .map { it.toUiModel() }
        
        val forSale = allProperties
            .filter { it.saleType == SaleType.SALE || it.saleType == SaleType.BOTH }
            .map { it.toUiModel() }

        val finalGroups = linkedMapOf<String, List<PropertyUiModel>>()
        if (forRent.isNotEmpty()) finalGroups["FOR_RENT"] = forRent
        if (forSale.isNotEmpty()) finalGroups["FOR_SALE"] = forSale
        
        // Add existing badge-based groups
        groupedByBadge.forEach { (badge, props) ->
            if (!finalGroups.containsKey(badge)) {
                finalGroups[badge] = props
            }
        }
        
        finalGroups
    }

    val isLoading = propertiesState.isLoading()
    val error = propertiesState.getErrorOrNull()?.message

    val scrollState = rememberScrollState()
    var isRefreshing by remember { mutableStateOf(false) }
    var pulledDistance by remember { mutableStateOf(0f) }
    val threshold = with(LocalDensity.current) { 120.dp.toPx() }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && scrollState.value == 0 && !isRefreshing) {
                    pulledDistance += available.y
                    if (pulledDistance > threshold) {
                        isRefreshing = true
                        viewModel.refresh()
                    }
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (scrollState.value > 0) pulledDistance = 0f
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            isRefreshing = false
            pulledDistance = 0f
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(nestedScrollConnection)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeTopBar()

            Spacer(Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("searchQuery", searchQuery)
                    navController.navigate(Screen.Base.Search)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(20.dp))

            val categoryListState = rememberLazyListState()
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = categoryListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = categories,
                    key = { it.id }
                ) { category ->
                    val isSelected = remember(selectedCategoryId) {
                        if (category.id == "all") selectedCategoryId == null 
                        else selectedCategoryId == category.id
                    }
                    
                    CategoryChip(
                        label = category.name,
                        selected = isSelected,
                        onClick = { 
                            val newId = if (category.id == "all") null else category.id
                            if (selectedCategoryId != newId) {
                                viewModel.selectCategory(newId)
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            when {
                isLoading && !isRefreshing -> LoadingState()
                error != null -> ErrorState(message = error, onRetry = { viewModel.refresh() })
                propertyGroups.isEmpty() -> EmptyState(message = "No properties found")
                else -> {
                    propertyGroups.forEach { (type, props) ->
                        PropertySection(
                            title = getSectionTitle(type),
                            properties = props,
                            onSeeAll = { onSeeAllClick(type) },
                            onPropertyClick = onPropertyClick
                        )
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

// ─── Components ─────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "e",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "MALI ESTATES",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = Color.White,
            modifier = Modifier.size(28.dp).clickable { }
        )
        Spacer(Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Messages",
            tint = Color.White,
            modifier = Modifier.size(28.dp).clickable { }
        )
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onFilterClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            placeholder = { Text("search", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White
            ),
            singleLine = true
        )
        
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Tune, "Filter", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PropertySection(
    title: String,
    properties: List<PropertyUiModel>,
    onSeeAll: () -> Unit,
    onPropertyClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                "See all",
                modifier = Modifier.clickable { onSeeAll() },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(properties) { property ->
                PropertyCard(property = property, onClick = { onPropertyClick(property.id) })
            }
        }
    }
}

@Composable
fun PropertyCard(property: PropertyUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Box(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                AsyncImage(
                    model = property.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(Color.DarkGray)
                )
                
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        property.badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    property.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        property.location ?: "Unknown",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)) {
                                append(property.price)
                            }
                            property.priceUnit?.let { unit ->
                                withStyle(SpanStyle(fontSize = 12.sp, color = Color.Gray)) {
                                    append("/$unit")
                                }
                            }
                        }
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(property.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
        Text(message, color = Color.Gray)
    }
}

private fun getSectionTitle(type: String): String = when(type.uppercase()) {
    "HOUSE" -> "House"
    "LAND" -> "Land"
    "APARTMENT" -> "Apartment"
    "FOR_RENT" -> "For Rent"
    "FOR_SALE" -> "For Sale"
    else -> "Properties"
}

// ─── Helpers ────────────────────────────────────────────────────────────────

private fun Property.toUiModel() = PropertyUiModel(
    id = id,
    title = title,
    location = place?.name ?: placeName ?: location?.address ?: address ?: "Unknown",
    price = "RWF ${formatPrice(price)}",
    priceUnit = if (saleType == SaleType.RENT) "month" else null,
    badge = type.name,
    imageUrl = featuredImg,
    rating = 4.5f
)
