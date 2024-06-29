package kky.flab.lookaround.core.data

import kky.flab.lookaround.core.data.mapper.RecordMapper
import kky.flab.lookaround.core.database.dao.RecordDao
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
): RecordRepository {

    private val _recording: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val recording: StateFlow<Boolean> = _recording.asStateFlow()

    override suspend fun saveRecord(record: Record): Long {
        return recordDao.insertRecord(
            RecordMapper.domainToData(record)
        )
    }

    override fun getRecords(): Flow<List<Record>> =
        recordDao.getRecord().map { entities ->
            entities.map { RecordMapper.dataToDomain(it) }
        }

    override suspend fun updateRecord(record: Record) {
        recordDao.updateRecord(
            RecordMapper.domainToData(record)
        )
    }

    override suspend fun deleteRecord(record: Record) {
        recordDao.deleteRecord(
            RecordMapper.domainToData(record)
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