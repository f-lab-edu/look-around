package kky.flab.lookaround.core.domain.model

data class Record(
    val id: Long,
    val memo: String?,
//    val image: ByteArray?,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val path: List<Path>
)
