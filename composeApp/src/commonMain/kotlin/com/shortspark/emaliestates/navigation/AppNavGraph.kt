package com.shortspark.emaliestates.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.shortspark.emaliestates.auth.presentation.ChangePasswordScreen
import com.shortspark.emaliestates.auth.presentation.ForgotPasswordScreen
import com.shortspark.emaliestates.auth.presentation.SigninScreen
import com.shortspark.emaliestates.auth.presentation.Signup2Screen
import com.shortspark.emaliestates.auth.presentation.SignupScreen
import com.shortspark.emaliestates.auth.presentation.VerifyOtpScreen
import com.shortspark.emaliestates.home.presentation.HomeScreen
import com.shortspark.emaliestates.home.presentation.MainScreen


@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween (700)) },
        exitTransition = { fadeOut(animationSpec = tween (700)) }
    ) {

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

        navigation(
            route = Graph.BASE,
            startDestination = BaseScreen.Home.route
        ) {
            composable(BaseScreen.Home.route) {
                MainScreen(
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
                HomeScreen()
            }

            composable(BaseScreen.Search.route) {
                Text("Search Screen")
            }
        }
    }
}
