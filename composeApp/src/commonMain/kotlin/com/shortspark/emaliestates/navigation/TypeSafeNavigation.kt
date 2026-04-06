package com.shortspark.emaliestates.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Helper for creating type-safe navigation routes with arguments.
 * Uses Kotlin serialization to convert between route strings and typed objects.
 */

/**
 * Represents a navigation route that can be type-safe.
 */
interface TypeSafeRoute {
    val route: String
    val arguments: List<NamedNavArgument>
}

/**
 * Creates a type-safe route for a destination with a single serializable argument.
 * Example:
 *   val PropertyDetailRoute = SingleArgRoute("properties", PropertyDetailArgs.serializer())
 *   navController.navigate(PropertyDetailRoute.createRoute(PropertyDetailArgs("123")))
 */
class SingleArgRoute<T : Any>(
    private val baseRoute: String,
    private val serializer: KSerializer<T>,
    private val argName: String = "arg"
) : TypeSafeRoute {
    private val json = Json { }

    override val route: String = "$baseRoute/{$argName}"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(argName) {
            type = androidx.navigation.NavType.StringType
        }
    )

    fun createRoute(arg: T): String {
        val serialized = json.encodeToString(serializer, arg)
        return "$baseRoute/$serialized"
    }
}

/**
 * Creates a type-safe route using custom serializer/deserializer functions.
 */
inline fun <reified T : Any> typedRoute(
    baseRoute: String,
    crossinline toJson: (T) -> String,
    crossinline fromJson: (String) -> T?
): TypeSafeRoute = object : TypeSafeRoute {
    override val route: String = "$baseRoute/{arg}"
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument("arg") { type = androidx.navigation.NavType.StringType }
    )

    fun createRoute(arg: T): String = "$baseRoute/${toJson(arg)}"
    fun parseFromString(value: String): T? = fromJson(value)
}
