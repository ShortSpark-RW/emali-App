@file:Suppress("UnresolvedReference", "UnresolvedClassName")

package com.shortspark.emaliestates.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.presentation.ChangePasswordScreen
import com.shortspark.emaliestates.auth.presentation.ForgotPasswordScreen
import com.shortspark.emaliestates.auth.presentation.SigninScreen
import com.shortspark.emaliestates.auth.presentation.Signup2Screen
import com.shortspark.emaliestates.auth.presentation.SignupScreen
import com.shortspark.emaliestates.auth.presentation.VerifyOtpScreen
import com.shortspark.emaliestates.home.presentation.EditProfileScreen
import com.shortspark.emaliestates.home.presentation.MainScreenContainer
import com.shortspark.emaliestates.home.presentation.ProfileScreen
import com.shortspark.emaliestates.home.presentation.SplashScreen
import com.shortspark.emaliestates.property.presentation.PropertyDetailScreen
import com.shortspark.emaliestates.ui.screens.search.SearchScreen
import com.shortspark.emaliestates.ui.screens.search.SearchResultsScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Graph.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {

        // Splash Screen - decides where to navigate
        composable(Graph.SPLASH) {
            // Check if a token is stored locally to determine login status
            val authRepository: AuthRepository = koinInject()
            val hasToken = authRepository.getToken() != null

            SplashScreen(
                onReady = {
                    if (hasToken) {
                        navController.navigate(Graph.BASE) {
                            popUpTo(Graph.SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Graph.AUTHENTICATION) {
                            popUpTo(Graph.SPLASH) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Authentication Graph
        navigation(
            route = Graph.AUTHENTICATION,
            startDestination = AuthScreen.SignIn.route,
        ) {
            composable(AuthScreen.SignIn.route) {
                SigninScreen(navController)
            }
            composable(AuthScreen.SignUp.route) {
                SignupScreen(navController)
            }
            composable(AuthScreen.ForgotPassword.route) {
                ForgotPasswordScreen(navController)
            }
            composable(AuthScreen.VerifyOtp.route) {
                VerifyOtpScreen(navController)
            }
            composable(AuthScreen.ChangePassword.route) {
                ChangePasswordScreen(navController)
            }
            composable(AuthScreen.SignUp2.route) {
                Signup2Screen(navController)
            }
        }

        // Base (Main) Graph
        navigation(
            route = Graph.BASE,
            startDestination = "main_container"
        ) {
            // Main container with nested NavHost for bottom tabs
            composable("main_container") {
                MainScreenContainer(
                    outerNavController = navController
                )
            }

            // Standalone screens (not in bottom nav)
            composable(BaseScreen.EditProfile.route) {
                EditProfileScreen(navController = navController)
            }

            composable(BaseScreen.Search.route) {
                SearchScreen(navController = navController)
            }

            composable("search/results") {
                SearchResultsScreen(navController = navController)
            }

            composable(
                route = BaseScreen.PropertyDetail.route,
                arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val propertyId = backStackEntry.arguments?.getString("propertyId") ?: return@composable
                PropertyDetailScreen(
                    propertyId = propertyId,
                    navController = navController
                )
            }
        }
    }
}
