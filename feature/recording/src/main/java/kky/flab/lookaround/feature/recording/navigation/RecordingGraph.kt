package kky.flab.lookaround.feature.recording.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kky.flab.lookaround.core.ui_navigation.route.AppRoute
import kky.flab.lookaround.feature.recording.RecordingScreen

fun NavGraphBuilder.recordingGraph() {
    composable<AppRoute.Recording>(
        deepLinks = listOf(
            navDeepLink<AppRoute.Recording>(
                basePath = "lookaround://recording"
            )
        )
    ) {
        val askFinish = it.toRoute<AppRoute.Recording>().askFinish
        RecordingScreen(
            onFinish = {},
            askFinish = askFinish,
        )
    }
}
