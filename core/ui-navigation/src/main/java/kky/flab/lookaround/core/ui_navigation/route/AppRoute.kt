package kky.flab.lookaround.core.ui_navigation.route

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Main : AppRoute

    @Serializable
    data object Recording : AppRoute
}