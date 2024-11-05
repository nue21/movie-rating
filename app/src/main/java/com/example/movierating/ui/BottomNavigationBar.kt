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
                icon = { },
                label = { Text(screen.capitalize()) },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
