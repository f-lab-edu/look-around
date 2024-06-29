package kky.flab.lookaround.core.database.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PathEntity(
    val latitude: Double,
    val longitude: Double
)
