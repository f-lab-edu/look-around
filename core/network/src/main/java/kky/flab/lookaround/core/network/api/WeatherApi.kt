package kky.flab.lookaround.core.network.api

import kky.flab.lookaround.core.network.model.NetworkResult
import kky.flab.lookaround.core.network.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst")
    suspend fun getWeather(
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 60,
        @Query("dataType") dataType: String = "JSON",
        @Query("serviceKey", encoded = true) serviceKey: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
    ): NetworkResult<WeatherResponse>
}