package com.aleon.proyectocellcli.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AddOutlayScreen : Screen("add_outlay_screen")
    data object Outlay : Screen("outlay")
    object SettingsScreen : Screen("settings_screen")
}
