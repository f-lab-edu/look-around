package kky.flab.lookaround.feature.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.setting.SettingScreen

fun NavGraphBuilder.settingGraph() {
    composable<MainRoute.Setting> {
        SettingScreen()
    }
}