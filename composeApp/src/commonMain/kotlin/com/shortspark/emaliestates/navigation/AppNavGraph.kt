package com.shortspark.emaliestates.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shortspark.emaliestates.home.presentation.SplashScreen
import com.shortspark.emaliestates.home.viewModel.SplashEvent
import com.shortspark.emaliestates.home.viewModel.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.Splash,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        // Splash Screen
        composable<Screen.Splash> {
            val viewModel: SplashViewModel = koinViewModel()
            
            LaunchedEffect(Unit) {
                viewModel.checkAuthState()
            }

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        SplashEvent.Authenticated -> {
                            navController.navigate(NavGraph.Base) {
                                popUpTo(Screen.Splash) { inclusive = true }
                            }
                        }
                        SplashEvent.Unauthenticated -> {
                            navController.navigate(NavGraph.Auth) {
                                popUpTo(Screen.Splash) { inclusive = true }
                            }
                        }
                    }
                }
            }

            SplashScreen(
                onReady = {
                    // viewModel handles navigation via events
                }
            )
        }

        // Authentication Graph
        authGraph(navController)

        // Base (Main) Graph
        baseGraph(navController)
    }
}
