package com.example.myjourney.ui.Navigation

sealed class Screen (val route: String) {
    object HomeScreen : Screen("home")
    object Favorites : Screen("favorites")
    object Library : Screen("library")
    object Profile : Screen("profile")

    object CreateJournal: Screen("create_journal")

    object JournalDetail : Screen("journalDetail")


    object Login : Screen("login")

    object SignUp : Screen("signup")
}


