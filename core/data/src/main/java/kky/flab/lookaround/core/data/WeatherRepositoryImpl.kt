package kky.flab.lookaround.core.data

import kky.flab.lookaround.core.data.mapper.toDomain
import kky.flab.lookaround.core.domain.WeatherRepository
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.network.api.WeatherApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
): WeatherRepository {
    override suspend fun getRealTimeWeather(nx: Int, ny: Int): Weather {
        val serviceKey = BuildConfig.WEATHER_SERVICE_KEY

        val current = GregorianCalendar()
        val passHalfOfHour = current.get(Calendar.MINUTE) > 30

        //30분이 넘어가지 않았으면 이전 시간으로 조회(예보가 30분 단위)
        if (!passHalfOfHour)
            current.add(Calendar.HOUR, -1)

        val date = SimpleDateFormat("yyyyMMdd HH30", Locale.getDefault()).format(current.time).split(" ")

        val result = weatherApi.getWeather(
            nx = nx,
            ny = ny,
            baseDate = date[0],
            baseTime = date[1],
            serviceKey = serviceKey
        )

        return result.response.toDomain()
    }
}