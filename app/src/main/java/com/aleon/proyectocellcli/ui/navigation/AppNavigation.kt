import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aleon.proyectocellcli.ui.navigation.BottomNavigationBar
import com.aleon.proyectocellcli.ui.navigation.Screen
import com.aleon.proyectocellcli.ui.screens.AddOutlayScreen
import com.aleon.proyectocellcli.ui.screens.HomeScreen
import com.aleon.proyectocellcli.ui.screens.OutlayScreen
import com.aleon.proyectocellcli.ui.screens.SettingsScreen

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
            composable(
                route = Screen.AddOutlayScreen.ROUTE_WITH_ARGS,
                arguments = listOf(navArgument(Screen.AddOutlayScreen.ARG_EXPENSE_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) {
                AddOutlayScreen(navController = navController)
            }
            composable(Screen.OutlayScreen.route) {
                OutlayScreen(navController = navController)
            }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen()
            }
        }
    }
}
