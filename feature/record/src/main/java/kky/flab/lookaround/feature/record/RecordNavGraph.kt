package kky.flab.lookaround.feature.record

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kky.flab.lookaround.core.ui_navigation.route.MainRoute

fun NavGraphBuilder.recordGraph(
    onModifyClick: (Long) -> Unit,
) {
    composable<MainRoute.Record> {
        RecordScreen(onModifyClick = onModifyClick)
    }
}
