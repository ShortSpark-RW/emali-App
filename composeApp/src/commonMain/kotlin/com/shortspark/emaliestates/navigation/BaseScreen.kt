package com.shortspark.emaliestates.navigation

sealed class BaseScreen(val route: String) {
    object Home : BaseScreen(route = "home_screen")
    object Map : BaseScreen(route = "map_screen")
    object Tours : BaseScreen(route = "tours_screen")
    object Profile : BaseScreen(route = "profile_screen")
    object Search : BaseScreen(route = "search_screen")

}