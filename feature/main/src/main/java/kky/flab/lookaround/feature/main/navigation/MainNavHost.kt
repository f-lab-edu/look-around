package kky.flab.lookaround.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.home.navigation.homeGraph
import kky.flab.lookaround.feature.record.recordGraph
import kky.flab.lookaround.feature.setting.navigation.settingGraph

@Composable
fun MainNavHost(
    navController: NavHostController,
    onStartRecordingService: () -> Unit,
    onRouteRecording: () -> Unit,
    onShowSnackBar: (String) -> Unit,
    onModifyRecord: (Long) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home
    ) {
        homeGraph(
            onStartRecordingService = onStartRecordingService,
            onRouteRecording = onRouteRecording,
            onShowSnackBar = onShowSnackBar,
        )

        recordGraph(onModifyClick = onModifyRecord)

        settingGraph()
    }
}