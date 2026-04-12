package com.shortspark.emaliestates.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Splash : Screen
    
    @Serializable sealed interface Auth : Screen {
        @Serializable data object SignIn : Auth
        @Serializable data object SignUp : Auth
        @Serializable data object ForgotPassword : Auth
        @Serializable data object VerifyOtp : Auth
        @Serializable data object ChangePassword : Auth
        @Serializable data object SignUp2 : Auth
    }

    @Serializable sealed interface Base : Screen {
        @Serializable data object MainContainer : Base
        @Serializable data object Home : Base
        @Serializable data object Map : Base
        @Serializable data object Tours : Base
        @Serializable data object Profile : Base
        @Serializable data object EditProfile : Base
        @Serializable data object Search : Base
        @Serializable data object SearchResults : Base
        @Serializable data class PropertyDetail(val propertyId: String) : Base
    }
}

@Serializable
sealed interface NavGraph {
    @Serializable data object Auth : NavGraph
    @Serializable data object Base : NavGraph
}
