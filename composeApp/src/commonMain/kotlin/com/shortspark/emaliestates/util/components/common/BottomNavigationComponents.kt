package com.shortspark.emaliestates.util.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// ─── Bottom Nav Destinations ─────────────────────────────────────────────────

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Map : BottomNavItem("map", "Map", Icons.Filled.Map, Icons.Outlined.Map)
    object Tours : BottomNavItem("tours", "eTours", Icons.Outlined.PlayCircle, Icons.Outlined.PlayCircle)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Map,
    BottomNavItem.Tours,
    BottomNavItem.Profile
)

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    onAddClick: () -> Unit
) {
    // Determine current selected item from back stack
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left two items
            items.take(2).forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = item.route == currentRoute,
                    onClick = {
                        // Navigate to the selected route, avoiding duplicates
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                )
            }

            // Center FAB (Add Button)
            AddFabButton(onClick = onAddClick)

            // Right two items
            items.drop(2).forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = item.route == currentRoute,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = if (isSelected) item.selectedIcon else item.unselectedIcon
    val labelColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val iconColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        // eTours label has orange "e"
        if (item == BottomNavItem.Tours) {
            EToursLabel(isSelected = isSelected)
        } else {
            Text(
                text = item.label,
                color = labelColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun EToursLabel(isSelected: Boolean) {
    val baseColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    Row {
        Text(
            text = "e",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "tours",
            color = baseColor,
            fontSize = 11.sp
        )
    }
}

@Composable
fun AddFabButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 28.sp
        )
    }
}
