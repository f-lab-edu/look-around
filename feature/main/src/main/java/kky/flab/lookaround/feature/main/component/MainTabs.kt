package kky.flab.lookaround.feature.main.component

import androidx.annotation.DrawableRes
import kky.flab.lookaround.feature.main.R

enum class MainTabs(
    @DrawableRes val iconResourceId: Int,
    val label: String,
) {
    Home(R.drawable.main_bottom_home, label = "홈"),
    Record(R.drawable.main_bottom_record, label = "기록"),
    Setting(R.drawable.main_bottom_setting, label = "설정");
}