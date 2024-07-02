package kky.flab.lookaround.feature.home.model

internal sealed interface UiState

internal data object Loading: UiState

internal data class Result(
    val recording: Boolean
): UiState