package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.model.Weather

sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Result(
        val temperatures: String,
        val precipitation: String,
        val windSpeed: String,
        val sky: Weather.Sky,
    ) : WeatherUiState

    data class Fail(val message: String) : WeatherUiState
}
