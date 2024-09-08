package kky.flab.lookaround.core.domain.const

enum class SummaryFilter(
    val days: Int
) {
    WEEK(7),
    MONTH(30),
    YEAR(365),
}