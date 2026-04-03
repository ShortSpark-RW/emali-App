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
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.auth.presentation.ChangePasswordScreen
import com.shortspark.emaliestates.auth.presentation.ForgotPasswordScreen
import com.shortspark.emaliestates.auth.presentation.SigninScreen
import com.shortspark.emaliestates.auth.presentation.Signup2Screen
import com.shortspark.emaliestates.auth.presentation.SignupScreen
import com.shortspark.emaliestates.auth.presentation.VerifyOtpScreen
import com.shortspark.emaliestates.home.presentation.MainScreen1
import com.shortspark.emaliestates.home.presentation.SplashScreen
import com.shortspark.emaliestates.property.presentation.PropertyDetailScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Graph.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {

        // Splash Screen - decides where to navigate
        composable(Graph.SPLASH) {
            val authViewModel: AuthViewModel = koinViewModel()
            val currentUser by authViewModel.currentUser.collectAsState(initial = null)

            LaunchedEffect(currentUser) {
                // Small delay to show splash at least briefly
                kotlinx.coroutines.delay(500)
            }

            SplashScreen(
                onReady = {
                    if (currentUser != null) {
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
            startDestination = BaseScreen.Home.route
        ) {
            composable(BaseScreen.Home.route) {
                MainScreen1(
                    navController = navController
                )
            }

            composable(BaseScreen.Map.route) {
                Text("Map Screen")
            }

            composable(BaseScreen.Tours.route) {
                Text("Tours Screen")
            }

            composable(BaseScreen.Profile.route) {
                Text("Profile Screen")
            }

            composable(BaseScreen.Search.route) {
                Text("Search Screen")
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
