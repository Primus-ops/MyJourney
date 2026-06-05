package com.example.myjourney.ui.Navigation

import android.provider.ContactsContract
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myjourney.ui.components.BottomNavigationBar
import com.example.myjourney.ui.screens.CreateScreen
import com.example.myjourney.ui.screens.EditJournalScreen
import com.example.myjourney.ui.screens.FavoriteScreen
import com.example.myjourney.ui.screens.HomeScreen
import com.example.myjourney.ui.screens.JournalDetail
import com.example.myjourney.ui.screens.LibraryScreen
import com.example.myjourney.ui.screens.LibraryListScreen
import com.example.myjourney.ui.screens.LoginScreen
import com.example.myjourney.ui.screens.ProfileScreen
import com.example.myjourney.ui.screens.SignUpScreen

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.HomeScreen.route,
        Screen.Favorites.route,
        Screen.Library.route,
        Screen.Profile.route
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.Favorites.route) {
            FavoriteScreen(navController)
        }
        composable(Screen.Library.route) {
            LibraryScreen(navController)
        }
        composable(
            route = "${Screen.LibraryList.route}/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "drafts"
            LibraryListScreen(
                type = type,
                navController = navController
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange
            )
        }

        composable(Screen.CreateJournal.route) {
            CreateScreen(navController)
        }

        composable( //Journal Detail
            route = "${Screen.JournalDetail.route}/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("journalId") ?: 0
            JournalDetail(
                entryId = journalId,
                navController = navController
            )
        }

        composable( // Edit Journal
            route = "${Screen.EditJournal.route}/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("journalId") ?: 0
            EditJournalScreen(
                journalId = journalId,
                navController = navController
            )
        }
    }
}
