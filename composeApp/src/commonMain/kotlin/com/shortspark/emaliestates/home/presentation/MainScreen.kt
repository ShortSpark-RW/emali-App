package com.shortspark.emaliestates.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.auth.viewModel.AuthViewModel
import com.shortspark.emaliestates.data.remote.AuthApi
import com.shortspark.emaliestates.data.repository.AuthRepository
import com.shortspark.emaliestates.home.presentation.bottomNavigation.bottomNavItems
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.navigation.Graph
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    navController: NavController
) {
    val authRepository: AuthRepository = koinInject()
    val authViewModel = koinInject<AuthViewModel>()


    var selected by remember {
        mutableIntStateOf(0)
    }

    val rootNavController = rememberNavController()
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed {
                        index, item ->
                    val isSelected = item.title.lowercase() ==
                            navBackStackEntry?.destination?.route
                    NavigationBarItem(
//                            selected = index == selected,
                        selected = isSelected,
                        onClick = {
//                                selected = index
                            rootNavController.navigate(item.title.lowercase()) {
                                popUpTo(rootNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = {
                            Text(text = item.title)
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badges != 0) {
                                        Badge {
                                            Text(text = item.badges.toString())
                                        }
                                    } else if (item.hasNews) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
//                                        imageVector = if (index == selected) {
//                                            item.selectedIcon
//                                        } else item.unselectedIcon,
                                    imageVector = if (isSelected) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        },
                    )
                }
            }
        }
    ) {
        val token = authRepository.getToken()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Home Screen")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (token != null) {
                        navController.navigate(BaseScreen.Profile.route)
                    } else {
                        navController.navigate(Graph.AUTHENTICATION)
                    }
                }
            ) {

                Text("Sign In/Profile")
            }

            Button(
                onClick = {
                    navController.navigate(BaseScreen.Profile.route)
                }
            ) {

                Text("Base Screen")
            }

            if (token != null) {
                Button(
                    onClick = {
                        // 3. Perform Logout
                        authViewModel.logout()

                        // 4. Navigate back to Login Screen
                        navController.navigate(Graph.AUTHENTICATION) {
                            // Clear the backstack so they can't press "Back" to return here
                            popUpTo(Graph.BASE) { inclusive = true }
                        }
                    },
                ) {
                    Text("Sign Out")
                }
            }
        }
    }
}