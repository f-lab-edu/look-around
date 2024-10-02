package kky.flab.lookaround.feature.recording.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kky.flab.lookaround.core.ui_navigation.route.AppRoute
import kky.flab.lookaround.feature.recording.RecordingScreen

fun NavGraphBuilder.recordingGraph() {
    composable<AppRoute.Recording> {
        RecordingScreen(onFinish = {})
    }
}
