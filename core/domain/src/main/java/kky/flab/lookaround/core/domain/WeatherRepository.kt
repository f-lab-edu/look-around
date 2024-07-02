package kky.flab.lookaround.core.domain

import kky.flab.lookaround.core.domain.model.Weather

interface WeatherRepository {
    suspend fun getRealTimeWeather(
        nx: Int,
        ny: Int,
    ): Weather
}