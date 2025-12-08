package com.shortspark.emaliestates.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.shortspark.emaliestates.auth.presentation.SigninScreen


@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        navigation(
            route = Graph.AUTHENTICATION,
            startDestination = Screen.SignIn.route
        ) {
            composable(Screen.SignIn.route) {
                SigninScreen(navController)
            }
            composable(Screen.SignUp.route) {
                // TODO: Replace with your actual SignUpScreen
                Text("SignUp Screen")
            }
        }
        navigation(
            route = Graph.HOME,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                // TODO: Replace with your actual HomeScreen
                Text("Home Screen")
            }
        }
    }
}
