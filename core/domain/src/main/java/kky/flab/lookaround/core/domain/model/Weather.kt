package kky.flab.lookaround.core.domain.model

data class Weather(
    //강수량
    val precipitation: Double,
    //전반적인 날씨 상태
    val sky: Sky,
    //낙뢰 수치
    val thunderStroke: Double,
    //기온
    val temperatures: Int,
    //예보 시간
    val time: String
) {
    enum class Sky{
        SUNNY,
        CLOUDY,
        OVERCAST,
        RAINY,
        SNOW;
    }
}

