package kky.flab.lookaround.feature.main.component

import androidx.annotation.DrawableRes
import kky.flab.lookaround.core.ui_navigation.route.MainRoute
import kky.flab.lookaround.feature.main.R

enum class MainTabs(
    @DrawableRes val iconResourceId: Int,
    val label: String,
    val route: MainRoute,
) {
    Home(R.drawable.main_bottom_home, label = "홈", MainRoute.Home),
    Record(R.drawable.main_bottom_record, label = "기록", MainRoute.Record),
    Setting(R.drawable.main_bottom_setting, label = "설정", MainRoute.Setting);
}