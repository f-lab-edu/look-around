package kky.flab.lookaround.core.data.mapper

import kky.flab.lookaround.core.database.model.PathEntity
import kky.flab.lookaround.core.database.model.RecordEntity
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record

fun RecordEntity.toDomain(): Record = Record(
    id = id,
    startTimeStamp = startTimeStamp,
    endTimeStamp = endTimeStamp,
    memo = memo,
    distance = distance,
    path = path.map { Path(it.latitude, it.longitude) }
)

fun Record.toData(): RecordEntity = RecordEntity(
    id = id,
    startTimeStamp = startTimeStamp,
    endTimeStamp = endTimeStamp,
    memo = memo,
    path = path.map { PathEntity(it.latitude, it.longitude) },
    distance = distance,
    emoji = null,
//        image = null,
)
