package com.shortspark.emaliestates.navigation

sealed class AuthScreen(val route: String) {
    object SignIn : AuthScreen(route = "sign_in_screen")
    object SignUp : AuthScreen(route = "sign_up_screen")
    object ForgotPassword : AuthScreen(route = "forgot_password_screen")
}