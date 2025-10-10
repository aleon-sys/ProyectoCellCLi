package com.aleon.proyectocellcli.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aleon.proyectocellcli.ui.screens.AddOutlayScreen
import com.aleon.proyectocellcli.ui.screens.HomeScreen
import com.aleon.proyectocellcli.ui.screens.OutlayScreen
import com.aleon.proyectocellcli.ui.screens.SettingsScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                homeViewModel = hiltViewModel()
            )
        }
        composable(
            route = Screen.AddOutlay.route,
            arguments = listOf(navArgument("expenseId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) {
            AddOutlayScreen(
                navController = navController
            )
        }
        composable(Screen.Outlay.route) {
            OutlayScreen(
                navController = navController
            )
        }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen()
            }
        }
    }
}
