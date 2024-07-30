package kky.flab.lookaround.core.domain.model

data class Record(
    val id: Long,
    val memo: String?,
//    val image: ByteArray?,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val path: List<Path>,
    val distance: Long
) {
    companion object {
        val EMPTY = Record(
            id = 0,
            memo = "",
            startTimeStamp = 0,
            endTimeStamp = 0,
            distance = 0,
            path = emptyList(),
        )
    }
}
