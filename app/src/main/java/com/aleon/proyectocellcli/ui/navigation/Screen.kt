package com.aleon.proyectocellcli.ui.navigation

sealed interface Screen {
    val route: String

    object HomeScreen : Screen {
        override val route: String = "home_screen"
    }
    object AddOutlayScreen : Screen {
        override val route: String = "add_outlay_screen"
        const val ROUTE_WITH_ARGS = "add_outlay_screen?expenseId={expenseId}"
        const val ARG_EXPENSE_ID = "expenseId"
        fun withArgs(expenseId: Int): String {
            return "add_outlay_screen?expenseId=$expenseId"
        }
    }
    object OutlayScreen : Screen {
        override val route: String = "outlay_screen"
    }
    object SettingsScreen : Screen {
        override val route: String = "settings_screen"
    }
}
