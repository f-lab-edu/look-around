package kky.flab.lookaround.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val header: WeatherHeader,
    val body: WeatherBody
)

@JsonClass(generateAdapter = true)
data class WeatherHeader(
    val resultCode: String,
    val resultMsg: String,
)

@JsonClass(generateAdapter = true)
data class WeatherBody(
    val dataType: String,
    val items: WeatherItems
)

@JsonClass(generateAdapter = true)
data class WeatherItems(
    val item: List<WeatherItem>
)

@JsonClass(generateAdapter = true)
data class WeatherItem(
    val fcstDate: String,
    val fcstTime: String,
    //예보 값(ex: 기상 예보 코드가 강수량일 경우 mm)
    private val fcstValue: String,
    //기상 예보 코드(강수 형태, 강수량 등)
    val category: String,
    val baseDate: String,
    val baseTime: String,
    val nx: Int,
    val ny: Int,
) {
    val value: Double
        get() = try {
            fcstValue.toDouble()
        } catch (e: NumberFormatException) {
            if (category == "RN1") {
                getPrecipitationValue()
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }

    private fun getPrecipitationValue(): Double = when(fcstValue) {
        "1mm 미만" -> 0.5
        "강수없음" -> 0.0
        else -> {
            fcstValue.substringBefore("m").toDouble()
        }
    }
}