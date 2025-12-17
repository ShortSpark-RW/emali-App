package com.shortspark.emaliestates.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.shortspark.emaliestates.auth.presentation.SigninScreen
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
    ) {
        navigation(
            route = Graph.AUTHENTICATION,
            startDestination = AuthScreen.SignIn.route
        ) {
            composable(AuthScreen.SignIn.route) {
                SigninScreen(navController)
            }
            composable(AuthScreen.SignUp.route) {
                Text("SignUp Screen")
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
