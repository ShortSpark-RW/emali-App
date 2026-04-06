package com.shortspark.emaliestates.navigation

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Type-safe navigation arguments using Kotlin serialization.
 * Provides extension functions to pass and retrieve typed arguments.
 */

@Serializable
data class PropertyDetailArgs(
    val propertyId: String
) {
    companion object {
        private val json = Json { prettyPrint = true }
        private const val ROUTE = "properties/{args}"

        fun route(args: PropertyDetailArgs): String = "properties/${json.encodeToString(PropertyDetailArgs.serializer(), args)}"

        fun fromJson(jsonString: String): PropertyDetailArgs? = runCatching {
            json.decodeFromString(PropertyDetailArgs.serializer(), jsonString)
        }.getOrNull()
    }
}

// Extension to safely retrieve typed arguments from NavBackStackEntry
fun <T : Any> NavBackStackEntry.typedArgument(
    key: String,
    deserializer: (String) -> T?
): T? {
    return arguments?.getString(key)?.let { deserializer(it) }
}

// Example usage in AppNavGraph:
//
// val propertyDetailArgs = navArgument("args") {
//     type = NavType.StringType
// }
//
// composable(
//     route = BaseScreen.PropertyDetail.route,
//     arguments = listOf(propertyDetailArgs)
// ) { backStackEntry ->
//     val argsString = backStackEntry.arguments?.getString("args")
//     val args = argsString?.let { PropertyDetailArgs.fromJson(it) }
//     if (args != null) {
//         PropertyDetailScreen(propertyId = args.propertyId, ...)
//     }
// }
