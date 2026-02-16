package com.example.myjourney.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myjourney.R
import com.example.myjourney.model.BottomNavItem
import com.example.myjourney.ui.Navigation.Screen
import com.example.myjourney.ui.theme.MyJourneyTheme
import java.util.Locale

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem(Screen.HomeScreen, Icons.Default.Home),
        BottomNavItem(Screen.Favorites, Icons.Default.Favorite),
        BottomNavItem(Screen.Library, Icons.Default.Menu),
        BottomNavItem(Screen.Profile, Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            // Pop up to start destination to avoid multiple copies
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true  // Avoid multiple copies
                            restoreState = true     // Restore previous state
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.screen.route) },
                label = {
                    Text(item.screen.route.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    })
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    MyJourneyTheme {
        BottomNavigationBar(
            navController = rememberNavController()
        )
    }
}