package com.shortspark.emaliestates

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.navigation.AppNavGraph
import com.shortspark.emaliestates.navigation.Graph
import com.shortspark.emaliestates.theme.EmaliEstatesTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    EmaliEstatesTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController, startDestination = Graph.BASE)
    }
}
