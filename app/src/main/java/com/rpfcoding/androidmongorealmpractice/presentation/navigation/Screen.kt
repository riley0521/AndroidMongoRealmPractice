package com.rpfcoding.androidmongorealmpractice.presentation.navigation

sealed class Screen(val route: String) {
    object Authentication: Screen("authentication_screen")
    object Home: Screen("home_screen")
}
