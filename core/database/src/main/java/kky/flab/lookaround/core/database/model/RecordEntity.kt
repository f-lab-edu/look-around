package kky.flab.lookaround.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val memo: String?,
    val emoji: String?,
//    val image: Bitmap?,
    val startTimeStamp: Long,
    val endTimeStamp: Long,
    val distance: Long,
    val path: List<PathEntity>
)
