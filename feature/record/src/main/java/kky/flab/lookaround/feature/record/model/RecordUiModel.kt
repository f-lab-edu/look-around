package kky.flab.lookaround.feature.record.model

import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.ui.util.millsToTimeFormat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN)

data class RecordUiModel(
    val id: Long,
    val memo: String,
    val image: File?,
    val date: String,
    val runTime: String,
    private val startTimeStamp: Long,
    private val endTimeStamp: Long,
    val path: List<Path>,
    val distance: String,
) {
    fun toDomain(): Record = Record(
        id = id,
        memo =  memo,
        image = image?.path ?: "",
        startTimeStamp = startTimeStamp,
        endTimeStamp = endTimeStamp,
        path = path,
        distance = distance.removeSuffix("m").toLong(),
    )
}

fun Record.toUiModel(): RecordUiModel = RecordUiModel(
    id = id,
    memo =  memo,
    image = if (image.isNotEmpty()) File(image) else null,
    date = dateFormat.format(startTimeStamp),
    runTime = (endTimeStamp - startTimeStamp).millsToTimeFormat(),
    startTimeStamp = startTimeStamp,
    endTimeStamp = endTimeStamp,
    path = path,
    distance = "${distance}m",
)
