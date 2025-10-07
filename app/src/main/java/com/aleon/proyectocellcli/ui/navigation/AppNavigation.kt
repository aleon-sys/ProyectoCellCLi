package com.aleon.proyectocellcli.ui.navigation

import DashboardScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aleon.proyectocellcli.ui.screens.AddOutlayScreen

import com.aleon.proyectocellcli.ui.screens.HomeScreen
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
                HomeScreen()
            }
            composable(Screen.AddOutlayScreen.route) {
                AddOutlayScreen()
            }
            composable(Screen.DashboardScreen.route) {
                DashboardScreen()
            }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen()
            }
        }
    }
}
