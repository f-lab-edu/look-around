package kky.flab.lookaround.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.home.HomeScreen

fun NavGraphBuilder.homeGraph(
    onRouteRecording: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    composable<MainRoute.Home> {
        HomeScreen(
            onRouteRecording = onRouteRecording,
            onShowSnackBar = onShowSnackBar
        )
    }
}