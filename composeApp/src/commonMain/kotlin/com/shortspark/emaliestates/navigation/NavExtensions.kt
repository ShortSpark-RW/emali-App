package com.shortspark.emaliestates.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.shortspark.emaliestates.auth.presentation.ChangePasswordScreen
import com.shortspark.emaliestates.auth.presentation.ForgotPasswordScreen
import com.shortspark.emaliestates.auth.presentation.SigninScreen
import com.shortspark.emaliestates.auth.presentation.Signup2Screen
import com.shortspark.emaliestates.auth.presentation.SignupScreen
import com.shortspark.emaliestates.auth.presentation.VerifyOtpScreen
import com.shortspark.emaliestates.home.presentation.EditProfileScreen
import com.shortspark.emaliestates.home.presentation.MainScreenContainer
import com.shortspark.emaliestates.property.presentation.PropertyDetailScreen
import com.shortspark.emaliestates.ui.screens.search.SearchScreen
import com.shortspark.emaliestates.ui.screens.search.SearchResultsScreen
import androidx.navigation.toRoute

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<NavGraph.Auth>(
        startDestination = Screen.Auth.SignIn
    ) {
        composable<Screen.Auth.SignIn> {
            SigninScreen(navController)
        }
        composable<Screen.Auth.SignUp> {
            SignupScreen(navController)
        }
        composable<Screen.Auth.ForgotPassword> {
            ForgotPasswordScreen(navController)
        }
        composable<Screen.Auth.VerifyOtp> {
            VerifyOtpScreen(navController)
        }
        composable<Screen.Auth.ChangePassword> {
            ChangePasswordScreen(navController)
        }
        composable<Screen.Auth.SignUp2> {
            Signup2Screen(navController)
        }
    }
}

fun NavGraphBuilder.baseGraph(navController: NavHostController) {
    navigation<NavGraph.Base>(
        startDestination = Screen.Base.MainContainer
    ) {
        composable<Screen.Base.MainContainer> {
            MainScreenContainer(outerNavController = navController)
        }

        composable<Screen.Base.EditProfile> {
            EditProfileScreen(navController = navController)
        }

        composable<Screen.Base.Search> {
            SearchScreen(navController = navController)
        }

        composable<Screen.Base.SearchResults> {
            SearchResultsScreen(navController = navController)
        }

        composable<Screen.Base.PropertyDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Screen.Base.PropertyDetail>()
            PropertyDetailScreen(
                propertyId = detail.propertyId,
                navController = navController
            )
        }
    }
}
