package com.shortspark.emaliestates.home.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.util.components.common.AppBottomNavigationBar
import com.shortspark.emaliestates.util.components.common.BottomNavItem
import org.koin.compose.koinInject

/**
 * Main container that hosts a nested NavHost for bottom navigation tabs.
 * Each tab has its own back stack, preserving state independently.
 */
@Composable
fun MainScreenContainer(outerNavController: NavController) {
    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                navController = nestedNavController,
                items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Map,
                    BottomNavItem.Tours,
                    BottomNavItem.Profile
                ),
                onAddClick = { /* TODO: Open add property */ }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = BaseScreen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(BaseScreen.Home.route) {
                HomeScreen(
                    navController = outerNavController,
                    onPropertyClick = { propertyId ->
                        outerNavController.navigate(BaseScreen.PropertyDetail.createRoute(propertyId))
                    },
                    onSeeAllClick = { /* TODO: Handle see all navigation */ }
                )
            }
            composable(BaseScreen.Map.route) {
                PlaceholderScreen("Map", outerNavController)
            }
            composable(BaseScreen.Tours.route) {
                PlaceholderScreen("eTours", outerNavController)
            }
            composable(BaseScreen.Profile.route) {
                ProfileScreen(navController = outerNavController)
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    name: String,
    navController: NavController? = null
) {
    val authViewModel: AuthViewModel = koinInject()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "$name Screen",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = {
                    authViewModel.logout()
                    navController?.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.BASE) { inclusive = true }
                    }
                }
            ) {
                Text("Sign Out")
            }
        }
    }
}
