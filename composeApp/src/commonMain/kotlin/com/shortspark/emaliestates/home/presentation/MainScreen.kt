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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shortspark.emaliestates.home.presentation.bottomNavigation.bottomNavItems
import com.shortspark.emaliestates.navigation.BaseScreen
import com.shortspark.emaliestates.navigation.Graph

@Composable
fun MainScreen(
    navController: NavController
) {
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

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Home Screen")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    navController.navigate(Graph.AUTHENTICATION)
                }
            ) {

                Text("Sign In")
            }

            Button(
                onClick = {
                    navController.navigate(BaseScreen.Profile.route)
                }
            ) {

                Text("Base Screen")
            }
        }
    }
}