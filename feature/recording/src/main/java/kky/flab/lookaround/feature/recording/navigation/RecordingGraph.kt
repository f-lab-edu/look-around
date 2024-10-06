package kky.flab.lookaround.feature.recording.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kky.flab.lookaround.core.ui_navigation.route.AppRoute
import kky.flab.lookaround.feature.recording.ModifyRecordScreen
import kky.flab.lookaround.feature.recording.RecordingScreen

fun NavGraphBuilder.recordingGraph(
    onFinishRecord: (Long) -> Unit,
    onClose: () -> Unit,
) {
    composable<AppRoute.Recording>(
        deepLinks = listOf(
            navDeepLink<AppRoute.Recording>(
                basePath = "lookaround://recording"
            )
        )
    ) {
        val askFinish = it.toRoute<AppRoute.Recording>().askFinish
        RecordingScreen(
            onFinish = onFinishRecord,
            askFinish = askFinish,
        )
    }

    composable<AppRoute.ModifyRecord> {
        val id = it.toRoute<AppRoute.ModifyRecord>().id
        ModifyRecordScreen(
            id = id,
            onComplete = onClose,
        )
    }
}
