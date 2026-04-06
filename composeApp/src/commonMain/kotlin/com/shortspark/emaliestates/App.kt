package com.shortspark.emaliestates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.shortspark.emaliestates.navigation.AppNavGraph
import com.shortspark.emaliestates.theme.EmaliEstatesTheme
import com.shortspark.emaliestates.util.helpers.AppConstants
import com.sunildhiman90.kmauth.core.KMAuthConfig
import com.sunildhiman90.kmauth.core.KMAuthInitializer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Initialize KMAuth (config only - context is set in Android MainActivity)
    val initialized = remember { KMAuthInitializer.initialize(
        KMAuthConfig.forGoogle(webClientId = AppConstants.WEB_CLIENT_ID)
    ) }

//    KMAuthInitializer.initialize(
//        KMAuthConfig.forSupabase(
//            supabaseUrl = AppConstants.SUPABASE_URL,
//            supabaseKey = AppConstants.SUPABASE_KEY
//        )
//    )
    // ── Coil: register Ktor-based network fetcher (works on Android + iOS) ────
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .build()
    }

    EmaliEstatesTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController) // startDestination defaults to SPLASH
    }
}
