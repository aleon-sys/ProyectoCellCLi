package com.aleon.proyectocellcli.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    data object AddOutlay : Screen("add_outlay?expenseId={expenseId}") {        fun createRoute(expenseId: Long) = "add_outlay?expenseId=$expenseId"    }
    data object Outlay : Screen("outlay")
    object SettingsScreen : Screen("settings_screen")
}
