package com.example.movierating.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(backgroundColor = Color.White) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val items = listOf("home", "rate", "search", "profile")

        items.forEach { screen ->
            BottomNavigationItem(
                icon = {  },
                label = { Text(screen) },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        // 홈화면 클릭 시 스택을 완전히 제거
                        if (screen == "home") {
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                inclusive = true
                            }
                            restoreState = false  // 홈화면 상태를 리셋
                        } else {
                            popUpTo(navController.graph.startDestinationRoute!!) {
                                saveState = true
                            }
                            restoreState = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
