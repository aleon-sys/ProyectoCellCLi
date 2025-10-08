package com.aleon.proyectocellcli.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object DashboardScreen : Screen("dashboard_screen")
    object SettingsScreen : Screen("settings_screen")

    object AddOutlayScreen : Screen("add_outlay_screen?expenseId={expenseId}") {
        fun withArgs(expenseId: Int): String {
            return "add_outlay_screen?expenseId=$expenseId"
        }
    }
}
