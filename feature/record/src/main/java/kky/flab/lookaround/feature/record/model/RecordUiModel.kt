package kky.flab.lookaround.feature.record.model

import android.net.Uri
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record

data class RecordUiModel(
    val id: Long,
    val memo: String,
    private val imageUri: String,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val path: List<Path>,
    val distance: Long
) {
    val ensureUri: Uri = Uri.parse(imageUri)

    val hasImage: Boolean = imageUri.isNotEmpty()

    fun toDomainModel(): Record = Record(
        id = id,
        memo = memo,
        imageUri = imageUri,
        startTimeStamp = startTimeStamp,
        endTimeStamp = endTimeStamp,
        path = path,
        distance = distance
    )
}

fun Record.toUiModel(): RecordUiModel = RecordUiModel(
    id = id,
    memo = memo,
    imageUri = imageUri,
    startTimeStamp = startTimeStamp,
    endTimeStamp = endTimeStamp,
    path = path,
    distance = distance
)
