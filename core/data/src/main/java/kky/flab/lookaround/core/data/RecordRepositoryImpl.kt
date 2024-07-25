package kky.flab.lookaround.core.data

import android.location.Location
import kky.flab.lookaround.core.data.mapper.toData
import kky.flab.lookaround.core.data.mapper.toDomain
import kky.flab.lookaround.core.database.dao.RecordDao
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
) : RecordRepository {

    private val _recording: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val recording: Flow<Boolean> = _recording.asStateFlow()

    private val _recordingState: MutableStateFlow<Record> = MutableStateFlow(Record.EMPTY)
    override val recordingState: Flow<Record> = _recordingState.asStateFlow()

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
        _recordingState.value = Record.EMPTY.copy(startTimeStamp = System.currentTimeMillis())
        _recording.value = true
    }

    override suspend fun endRecording() {
        _recording.value = false
        val record = _recordingState.value
        _recordingState.value = Record.EMPTY
        saveRecord(record)
    }

    override fun addPath(path: Path) {
        if (_recordingState.value.path.isEmpty()) {
            updateState(path, 0)
        } else {
            if (distance(path) < MIN_WALK_DISTANCE) return
            updateState(path, distance(path))
        }
    }

    private fun updateState(path: Path, distance: Long) {
        _recordingState.update {
            it.copy(
                path = it
                    .path
                    .toMutableList()
                    .apply { add(path) },
                distance = it.distance + distance
            )
        }
    }

    private fun distance(path: Path): Long {
        val previousValue = _recordingState.value.path.last()

        val previous = Location("previous").apply {
            latitude = previousValue.latitude
            longitude = previousValue.longitude
        }

        val next = Location("next").apply {
            latitude = path.latitude
            longitude = path.longitude
        }

        return previous.distanceTo(next).toLong()
    }

    companion object {
        const val MIN_WALK_DISTANCE = 3
    }
}
