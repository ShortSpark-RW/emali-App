package com.shortspark.emaliestates.navigation

sealed class Screen(val route: String) {
    object SignIn : Screen(route = "sign_in_screen")
    object SignUp : Screen(route = "sign_up_screen")
    object Home : Screen(route = "home_screen")
}