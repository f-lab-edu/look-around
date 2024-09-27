package kky.flab.lookaround.core.ui_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.home.HomeScreen
import kky.flab.lookaround.feature.record.RecordScreen
import kky.flab.lookaround.feature.setting.SettingScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    onShowSnackBar: (String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home
    ) {
        composable<MainRoute.Home> {
            HomeScreen(
                onShowSnackBar = onShowSnackBar
            )
        }
        composable<MainRoute.Record> {
            RecordScreen()
        }
        composable<MainRoute.Setting> {
            SettingScreen()
        }
    }
}