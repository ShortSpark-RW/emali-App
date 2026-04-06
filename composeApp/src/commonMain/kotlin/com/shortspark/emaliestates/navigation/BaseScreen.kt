package com.shortspark.emaliestates.navigation

sealed class BaseScreen(val route: String) {
    object Home : BaseScreen(route = "home")
    object Map : BaseScreen(route = "map")
    object Tours : BaseScreen(route = "tours")
    object Profile : BaseScreen(route = "profile")
    object EditProfile : BaseScreen(route = "edit_profile")
    object Search : BaseScreen(route = "search")

    object PropertyDetail : BaseScreen("properties/{propertyId}") {
        fun createRoute(propertyId: String) = "properties/$propertyId"
    }
}