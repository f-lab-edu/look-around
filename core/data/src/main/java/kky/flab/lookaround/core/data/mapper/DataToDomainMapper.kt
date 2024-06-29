package kky.flab.lookaround.core.data.mapper

import kky.flab.lookaround.core.database.model.PathEntity
import kky.flab.lookaround.core.database.model.RecordEntity
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record

internal object RecordMapper {
    fun dataToDomain(dataModel: RecordEntity) = Record(
        id = dataModel.id,
        startTimeStamp = dataModel.startTimeStamp,
        endTimeStamp = dataModel.endTimeStamp,
        memo = dataModel.memo,
        path = dataModel.path.map { Path(it.latitude, it.longitude) }
    )

    fun domainToData(domainModel: Record) = RecordEntity(
        id = domainModel.id,
        startTimeStamp = domainModel.startTimeStamp,
        endTimeStamp = domainModel.endTimeStamp,
        memo = domainModel.memo,
        path = domainModel.path.map { PathEntity(it.latitude, it.longitude) },
        emoji = null,
//        image = null,
    )
}