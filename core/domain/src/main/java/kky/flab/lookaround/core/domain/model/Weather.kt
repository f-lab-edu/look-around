package kky.flab.lookaround.core.domain.model

data class Weather(
    val precipitation: Double,
    val sky: Sky,
    val thunderStroke: Double,
    val temperatures: Int,
    val time: String,
    val windSpeed: Int,
) {
    enum class Sky{
        SUNNY,
        CLOUDY,
        OVERCAST,
        RAINY,
        SNOW;
    }
}

