package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.const.SummaryFilter
import java.time.LocalDate

data class UiState(
    val recording: Boolean,
    val initializedConfig: Boolean,
    val hasAskedLocationPermission: Boolean,
    val summaryFilter: SummaryFilter,
    val currentMonthRecordDate: List<LocalDate>,
    val weatherUiState: WeatherUiState,
    val summaryUiState: SummaryUiState,
) {
    companion object {
        val EMPTY = UiState(
            recording = false,
            initializedConfig = false,
            hasAskedLocationPermission = false,
            summaryFilter = SummaryFilter.WEEK,
            currentMonthRecordDate = emptyList(),
            weatherUiState = WeatherUiState.Loading,
            summaryUiState = SummaryUiState.Loading,
        )
    }
}
