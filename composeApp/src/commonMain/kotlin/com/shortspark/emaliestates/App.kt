package com.shortspark.emaliestates

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.navigation.AppNavGraph
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.theme.EmaliEstatesTheme
import com.shortspark.emaliestates.util.helpers.AppConstants
import com.sunildhiman90.kmauth.core.KMAuthConfig
import com.sunildhiman90.kmauth.core.KMAuthInitializer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    KMAuthInitializer.initialize(
        KMAuthConfig.forGoogle(webClientId = AppConstants.WEB_CLIENT_ID)
    )

//    KMAuthInitializer.initialize(
//        KMAuthConfig.forSupabase(
//            supabaseUrl = AppConstants.SUPABASE_URL,
//            supabaseKey = AppConstants.SUPABASE_KEY
//        )
//    )

    EmaliEstatesTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController, startDestination = Graph.BASE)
    }
}
