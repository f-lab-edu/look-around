package kky.flab.lookaround.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kky.flab.lookaround.core.ui_navigation.route.AppRoute
import kky.flab.lookaround.feature.main.MainScreen
import kky.flab.lookaround.feature.recording.navigation.recordingGraph

@Composable
fun LookaroundApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRoute.Main,
    ) {
        composable<AppRoute.Main> {
            MainScreen {
                navController.navigate(AppRoute.Recording)
            }
        }

        recordingGraph()
    }
}