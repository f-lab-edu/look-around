package kky.flab.lookaround.core.data

import kky.flab.lookaround.core.data.mapper.toData
import kky.flab.lookaround.core.data.mapper.toDomain
import kky.flab.lookaround.core.database.dao.RecordDao
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
): RecordRepository {

    private val _recording: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val recording: Flow<Boolean> = _recording.asStateFlow()

    override suspend fun saveRecord(record: Record): Long {
        return recordDao.insertRecord(
            record.toData()
        )
    }

    override fun getRecords(): Flow<List<Record>> =
        recordDao.getRecord().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(
            record.toData()
        )
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.deleteRecord(
            record.toData()
        )
    }

    override fun startRecording() {
        _recording.value = true
    }

    override fun endRecording() {
        _recording.value = false
        //TODO: saveRecord()
    }

}