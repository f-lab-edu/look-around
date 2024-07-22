package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.model.Weather

data class UiState(
    val recording: Boolean,
    val weatherUiState: WeatherUiState,
    val recordState: RecordUiState
) {
    companion object {
        val EMPTY = UiState(
            recording = false,
            weatherUiState = WeatherUiState.Loading,
            recordState = RecordUiState.Loading
        )
    }
}