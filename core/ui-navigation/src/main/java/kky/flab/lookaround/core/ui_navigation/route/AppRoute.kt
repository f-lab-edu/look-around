package kky.flab.lookaround.core.ui_navigation.route

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Main : AppRoute

    @Serializable
    data class Recording(val askFinish: Boolean) : AppRoute

    @Serializable
    data class ModifyRecord(val id: Long) : AppRoute
}