package com.shortspark.emaliestates.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.navigation.Graph
import kotlinx.coroutines.flow.Flow

/**
 * Navigation commands that can be emitted from ViewModels.
 */
sealed class NavigationCommand {
    data object NavigateToHome : NavigationCommand()
    data object NavigateToProfile : NavigationCommand()
    data class NavigateToPropertyDetail(val propertyId: String) : NavigationCommand()
    data object NavigateToSearch : NavigationCommand()
    data object NavigateToEditProfile : NavigationCommand()
    data object NavigateToAuthentication : NavigationCommand()
    data class ShowToast(val message: String) : NavigationCommand()
}

object NavigationHandler {

    fun execute(command: NavigationCommand, navController: NavController) {
        when (command) {
            is NavigationCommand.NavigateToHome -> navigateToHome(navController)
            is NavigationCommand.NavigateToProfile -> navigateToProfile(navController)
            is NavigationCommand.NavigateToPropertyDetail -> navigateToPropertyDetail(navController, command.propertyId)
            is NavigationCommand.NavigateToSearch -> navigateToSearch(navController)
            is NavigationCommand.NavigateToEditProfile -> navigateToEditProfile(navController)
            is NavigationCommand.NavigateToAuthentication -> navigateToAuthentication(navController)
            is NavigationCommand.ShowToast -> showToast(command.message)
        }
    }

    private fun navigateToHome(navController: NavController) {
        navController.navigate(Graph.BASE) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    private fun navigateToProfile(navController: NavController) {
        navController.navigate(BaseScreen.Profile.route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    private fun navigateToPropertyDetail(navController: NavController, propertyId: String) {
        val route = BaseScreen.PropertyDetail.createRoute(propertyId)
        navController.navigate(route)
    }

    private fun navigateToSearch(navController: NavController) {
        navController.navigate(BaseScreen.Search.route) {
            launchSingleTop = true
        }
    }

    private fun navigateToEditProfile(navController: NavController) {
        navController.navigate(BaseScreen.EditProfile.route) {
            launchSingleTop = true
        }
    }

    private fun navigateToAuthentication(navController: NavController) {
        navController.navigate(Graph.AUTHENTICATION) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    private fun showToast(message: String) {
        // TODO: Show a snackbar toast
    }
}

@Composable
fun handleNavigationEvents(
    navigationEvents: Flow<NavigationCommand>,
    navController: NavController
) {
    LaunchedEffect(navigationEvents, navController) {
        navigationEvents.collect { command ->
            NavigationHandler.execute(command, navController)
        }
    }
}
