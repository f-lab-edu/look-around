package kky.flab.lookaround.core.domain.model

data class Summary(
    val time: Long,
    val count: Int,
    val startTime: Long,
    val endTime: Long,
    val mostDayOfWeek: String,
)
