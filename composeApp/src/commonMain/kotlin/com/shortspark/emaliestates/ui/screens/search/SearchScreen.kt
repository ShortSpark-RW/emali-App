package com.shortspark.emaliestates.ui.screens.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.WindowInsets

/**
 * Search/Filter screen that allows users to filter properties
 * Dark theme with orange accents
 */
@Composable
fun SearchScreen(
    navController: NavController
) {
    // Retrieve initial query from SavedStateHandle (passed from HomeScreen)
    val initialQuery = navController.currentBackStackEntry?.savedStateHandle?.get<String>("searchQuery") ?: ""

    var selectedTab by remember { mutableStateOf(ListingTab.FOR_SALE) }
    var selectedPropertyType by remember { mutableStateOf(propertyTypes[0]) }
    var propertyTypeExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var minPrice by remember { mutableStateOf(0f) } // In millions
    var maxPrice by remember { mutableStateOf(5f) }
    var minSurface by remember { mutableStateOf(0f) } // In m²
    var maxSurface by remember { mutableStateOf(500f) }
    var selectedRooms by remember { mutableStateOf("Any") }
    var selectedBathrooms by remember { mutableStateOf("Any") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Header with tabs
        FilterScreenHeader(
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            onCancel = { navController.popBackStack() },
            onApply = {
                val filters = FilterState(
                    tab = selectedTab,
                    propertyType = selectedPropertyType,
                    query = searchQuery,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minSurface = minSurface,
                    maxSurface = maxSurface,
                    rooms = selectedRooms,
                    bathrooms = selectedBathrooms
                )
                navController.currentBackStackEntry?.savedStateHandle?.set("searchFilters", filters)
                navController.navigate("search/results")
            }
        )

        // Scrollable filter controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Text search field
            SectionLabel("Search by keyword")
            Spacer(Modifier.height(8.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(
                        "Enter keywords...",
                        color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else null,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Property type dropdown
            SectionLabel("Property type")
            Spacer(Modifier.height(8.dp))
            PropertyTypeDropdown(
                selected = selectedPropertyType,
                expanded = propertyTypeExpanded,
                options = propertyTypes,
                onToggle = { propertyTypeExpanded = !propertyTypeExpanded },
                onSelect = {
                    selectedPropertyType = it
                    propertyTypeExpanded = false
                }
            )

            Spacer(Modifier.height(24.dp))

            // Price range
            SectionLabel("Price range")
            Spacer(Modifier.height(6.dp))
            PriceRangeSlider(
                minValue = minPrice,
                maxValue = maxPrice,
                onMinChange = { minPrice = it },
                onMaxChange = { maxPrice = it },
                maxMillions = 5f
            )

            Spacer(Modifier.height(24.dp))

            // Rooms
            SectionLabel("Rooms")
            Spacer(Modifier.height(12.dp))
            OptionSelector(
                options = roomOptions,
                selected = selectedRooms,
                onSelect = { selectedRooms = it }
            )

            Spacer(Modifier.height(24.dp))

            // Bathrooms
            SectionLabel("Bathrooms")
            Spacer(Modifier.height(12.dp))
            OptionSelector(
                options = bathroomOptions,
                selected = selectedBathrooms,
                onSelect = { selectedBathrooms = it }
            )

            Spacer(Modifier.height(24.dp))

            // Surface Area
            SectionLabel("Surface Area")
            Spacer(Modifier.height(6.dp))
            SurfaceRangeSlider(
                value = maxSurface,
                onValueChange = { maxSurface = it },
                maxSurface = 500f,
                minSurface = minSurface
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Filter State ──────────────────────────────────────────────────────────────

data class FilterState(
    val tab: ListingTab,
    val propertyType: String,
    val query: String,
    val minPrice: Float,
    val maxPrice: Float,
    val minSurface: Float,
    val maxSurface: Float,
    val rooms: String,
    val bathrooms: String
)

// ─── Filter Screen Header ──────────────────────────────────────────────────────

@Composable
fun FilterScreenHeader(
    selectedTab: ListingTab,
    onTabSelect: (ListingTab) -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Action bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Cancel",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCancel
                    )
            )

            Text(
                text = "Filter your search",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = "Apply",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onApply
                    )
            )
        }

        // Tab row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ListingTab.entries.forEach { tab ->
                FilterTab(
                    label = tab.label,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelect(tab) }
                )
            }
        }

        // Bottom divider
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )
    }
}

@Composable
private fun FilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 200),
        label = "tab_color"
    )

    val underlineWidth by animateDpAsState(
        targetValue = if (isSelected) 36.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "underline_width"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(bottom = 10.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .height(2.5.dp)
                .width(underlineWidth)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.secondary)
        )
    }
}

// ─── Property Type Dropdown ────────────────────────────────────────────────────

@Composable
private fun PropertyTypeDropdown(
    selected: String,
    expanded: Boolean,
    options: List<String>,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onToggle)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onToggle() },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (option == selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    },
                    onClick = { onSelect(option) }
                )
            }
        }
    }
}

// ─── Section Label ────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

// ─── Price Range Slider ───────────────────────────────────────────────────────

@Composable
private fun PriceRangeSlider(
    minValue: Float,
    maxValue: Float,
    onMinChange: (Float) -> Unit,
    onMaxChange: (Float) -> Unit,
    maxMillions: Float = 5f
) {
    Column {
        Text(
            text = "${formatSliderValue(minValue, maxMillions)} - ${formatSliderValue(maxValue, maxMillions)}",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        Slider(
            value = minValue,
            onValueChange = { newMin ->
                if (newMin <= maxValue) onMinChange(newMin)
            },
            valueRange = 0f..maxMillions,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )

        Slider(
            value = maxValue,
            onValueChange = { newMax ->
                if (newMax >= minValue) onMaxChange(newMax)
            },
            valueRange = 0f..maxMillions,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Min", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            Text(text = "Max", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
        }
    }
}

// ─── Surface Range Slider ─────────────────────────────────────────────────────

@Composable
private fun SurfaceRangeSlider(
    minSurface: Float,
    value: Float,
    onValueChange: (Float) -> Unit,
    maxSurface: Float
) {
    Column {
        Text(
            text = "${minSurface.toInt()} m² - ${value.toInt()} m²",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = minSurface..maxSurface,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Min", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            Text(text = "Max", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
        }
    }
}

// ─── Option Selector ──────────────────────────────────────────────────────────

@Composable
private fun OptionSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(option) }
            ) {
                Text(
                    text = option,
                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────

private fun formatSliderValue(value: Float, maxMillions: Float): String {
    val millions = (value * maxMillions)
    return when {
        millions >= 1f -> "${millions.toInt()}M"
        else -> "${(millions * 1000).toInt()}K"
    }
}

// ─── Data ─────────────────────────────────────────────────────────────────────

enum class ListingTab(val label: String) {
    FOR_SALE("For Sale"),
    RECENTLY_SOLD("Recently sold"),
    FOR_RENT("For Rent")
}

private val propertyTypes = listOf(
    "Any",
    "Single-family home",
    "Apartment",
    "Duplex",
    "Villa",
    "Bungalow",
    "Land plot"
)

private val roomOptions = listOf("Any", "1", "2", "3", "4", "5+")
private val bathroomOptions = listOf("Any", "1", "2", "3", "4+")
