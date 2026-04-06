package com.shortspark.emaliestates.util.components.common

import androidx.navigation.NavController

/**
 * Navigate to a route, avoiding duplicate entries on the back stack.
 * Uses launchSingleTop and popUpTo to clear intermediate destinations.
 */
fun NavController.navigateSafely(route: String) {
    try {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(graph.startDestinationId) {
                saveState = true
            }
        }
    } catch (e: Exception) {
        // Navigation failed, could log error
        e.printStackTrace()
    }
}

/**
 * Navigate and clear the entire back stack up to the graph root.
 * Useful for logout or switching between auth and main graphs.
 */
fun NavController.navigateWithClearBackStack(route: String) {
    try {
        navigate(route) {
            popUpTo(graph.id) { inclusive = true }
            launchSingleTop = true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
