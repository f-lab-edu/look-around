package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.model.Weather

sealed interface WeatherUiState {
    data object Loading : WeatherUiState

    data class Result(val data: Weather) : WeatherUiState

    data class Fail(val message: String) : WeatherUiState
}