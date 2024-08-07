package kky.flab.lookaround.core.data.mapper

import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.network.model.WeatherResponse

fun WeatherResponse.toDomain(): Weather {
    val items = body.items.item.groupBy { it.category }.map { it.value.first() }

    val type: Double = items.find { it.category == PRECIPITATION_TYPE }?.value
        ?: error("강수형태 값이 없습니다.")

    val precipitation: Double = items.find { it.category == PRECIPITATION }?.value
        ?: error("강수량에 대한 값이 없습니다.")

    val sky: Int = items.find { it.category == SKY }?.value?.toInt()
        ?: error("하늘상태에 대한 값이 없습니다.")

    val temperature: Int = items.find { it.category == TEMPERATURES }?.value?.toInt()
        ?: error("기온에 대한 값이 없습니다.")

    val thunderStroke: Double = items.find { it.category == THUNDER_STROKE }?.value
        ?: error("낙뢰에 대한 값이 없습니다.")

    val windSpeed: Int = items.find { it.category == WIND_SPEED }?.value?.toInt()
        ?: error("풍속에 대한 값이 없습니다.")

    val skyType: Weather.Sky = when {
        sky == 1 && precipitation == 0.0 -> Weather.Sky.SUNNY
        sky == 3 && precipitation == 0.0 -> Weather.Sky.CLOUDY
        sky == 4 && precipitation == 0.0 -> Weather.Sky.OVERCAST
        (type.toInt() == 1 || type.toInt() == 2 || type.toInt() == 5) && precipitation > 0.0 -> Weather.Sky.RAINY
        else -> Weather.Sky.SNOW
    }

    return Weather(
        sky = skyType,
        precipitation = precipitation,
        thunderStroke = thunderStroke,
        temperatures = temperature,
        windSpeed = windSpeed,
        time = items.first().fcstTime,
    )
}