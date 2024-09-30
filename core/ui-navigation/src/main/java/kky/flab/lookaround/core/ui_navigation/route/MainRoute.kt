package kky.flab.lookaround.core.ui_navigation.route

import kotlinx.serialization.Serializable

sealed interface MainRoute {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Record : MainRoute

    @Serializable
    data object Setting : MainRoute
}
