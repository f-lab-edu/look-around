package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.const.SummaryFilter

data class UiState(
    val recording: Boolean,
    val initializedConfig: Boolean,
    val hasAskedLocationPermission: Boolean,
    val summaryFilter: SummaryFilter,
    val weatherUiState: WeatherUiState,
    val summaryUiState: SummaryUiState,
) {
    companion object {
        val EMPTY = UiState(
            recording = false,
            initializedConfig = false,
            hasAskedLocationPermission = false,
            summaryFilter = SummaryFilter.WEEK,
            weatherUiState = WeatherUiState.Loading,
            summaryUiState = SummaryUiState.Loading,
        )
    }
}
