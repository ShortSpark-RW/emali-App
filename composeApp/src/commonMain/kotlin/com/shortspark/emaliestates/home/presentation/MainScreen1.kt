package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.navigation.Graph
import org.koin.compose.koinInject

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

// ─── MainScreen ───────────────────────────────────────────────────────────────

@Composable
fun MainScreen1(navController: NavController) {
    var selectedRoute by remember { mutableStateOf(BottomNavItem.Home.route) }

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                selectedRoute = selectedRoute,
                onItemSelected = { selectedRoute = it },
                onAddClick = { /* open add property flow */ }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedRoute) {
                BottomNavItem.Home.route -> HomeScreen1(navController = navController)
                BottomNavItem.Map.route -> PlaceholderScreen("Map")
                BottomNavItem.Tours.route -> PlaceholderScreen("eTours")
                BottomNavItem.Profile.route -> PlaceholderScreen("Profile", navController = navController)
            }
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

@Composable
fun AppBottomNavigationBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    onAddClick: () -> Unit
) {
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
            bottomNavItems.take(2).forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = item.route == selectedRoute,
                    onClick = { onItemSelected(item.route) }
                )
            }

            // Center FAB (Add Button)
            AddFabButton(onClick = onAddClick)

            // Right two items
            bottomNavItems.drop(2).forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = item.route == selectedRoute,
                    onClick = { onItemSelected(item.route) }
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

// ─── Placeholder ─────────────────────────────────────────────────────────────

@Composable
fun PlaceholderScreen(
    name: String,
    navController: NavController? = null
) {
    val authRepository: AuthRepository = koinInject()
    val authViewModel = koinInject<AuthViewModel>()

    val rootNavController = rememberNavController()
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val token = authRepository.getToken()
        Text(
            text = "$name Screen",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Button(
            onClick = {
                // 3. Perform Logout
                authViewModel.logout()

                // 4. Navigate back to Login Screen
                navController?.navigate(Graph.AUTHENTICATION) {
                    // Clear the backstack so they can't press "Back" to return here
                    popUpTo(Graph.BASE) { inclusive = true }
                }
            },
        ) {
            Text("Sign Out")
        }

        if (token != null) {
            Button(
                onClick = {
                    // 3. Perform Logout
                    authViewModel.logout()

                    // 4. Navigate back to Login Screen
                    navController?.navigate(Graph.AUTHENTICATION) {
                        // Clear the backstack so they can't press "Back" to return here
                        popUpTo(Graph.BASE) { inclusive = true }
                    }
                },
            ) {
                Text("Sign Out")
            }
        }
    }
}