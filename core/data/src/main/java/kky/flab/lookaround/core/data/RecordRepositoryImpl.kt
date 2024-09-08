package kky.flab.lookaround.core.data

import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.location.Location
import kky.flab.lookaround.core.data.mapper.toData
import kky.flab.lookaround.core.data.mapper.toDomain
import kky.flab.lookaround.core.database.dao.RecordDao
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.domain.model.Summary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject

internal class RecordRepositoryImpl @Inject constructor(
    private val recordDao: RecordDao
) : RecordRepository {

    private val _recording: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val recording: Flow<Boolean> = _recording.asStateFlow()

    private val _recordingState: MutableStateFlow<Record> = MutableStateFlow(Record.EMPTY)
    override val recordingState: StateFlow<Record> = _recordingState.asStateFlow()

    override suspend fun saveRecord(record: Record): Long {
        return recordDao.insertRecord(
            record.toData()
        )
    }

    override fun getRecords(): Flow<List<Record>> =
        recordDao.getRecords().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getRecord(id: Long): Record = recordDao.getRecord(id).toDomain()

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

    override suspend fun endRecording(): Long {
        _recording.value = false
        val record = _recordingState.value.copy(endTimeStamp = System.currentTimeMillis())
        _recordingState.value = Record.EMPTY
        val id = saveRecord(record)
        return id
    }

    override fun addPath(path: Path) {
        if (_recordingState.value.path.isEmpty()) {
            updateState(path, 0)
        } else {
            if (distance(path) < MIN_WALK_DISTANCE) return
            updateState(path, distance(path))
        }
    }

    override fun getSummary(filter: SummaryFilter): Flow<Summary> {
        return recordDao.getRecords().map { entities ->
            if (entities.isEmpty()) {
                return@map Summary(
                    count = 0,
                    time = 0,
                    startTime = 0,
                    endTime = 0,
                    mostDayOfWeek = "-",
                )
            }

            val start = GregorianCalendar().apply {
                add(Calendar.DAY_OF_YEAR, -filter.days)
                set(Calendar.HOUR, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val end = GregorianCalendar()

            val searched =
                entities.filter { record -> record.startTimeStamp >= start.timeInMillis && record.endTimeStamp <= end.timeInMillis }
            val count = searched.size
            val time = searched.sumOf { record -> record.endTimeStamp - record.startTimeStamp }
            val bestDayOfWeek = searched
                .groupBy { record -> record.startTimeStamp.toDayOfWeek() }
                .maxBy { entry -> entry.value.size }
                .key

            Summary(
                count = count,
                time = time,
                mostDayOfWeek = bestDayOfWeek,
                startTime = start.timeInMillis,
                endTime = end.timeInMillis
            )
        }
    }

    private fun updateState(path: Path, distance: Long) {
        _recordingState.update { record ->
            record.copy(
                path = record
                    .path
                    .toMutableList()
                    .apply { add(path) },
                distance = record.distance + distance
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

    private fun Long.toDayOfWeek(): String =
        Calendar.getInstance().apply {
            time = Date(this@toDayOfWeek)
        }.run {
            when (get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "일"
                Calendar.MONDAY -> "월"
                Calendar.TUESDAY -> "화"
                Calendar.WEDNESDAY -> "수"
                Calendar.THURSDAY -> "목"
                Calendar.FRIDAY -> "금"
                Calendar.SATURDAY -> "토"
                else -> "Unknown"
            }
        }

    companion object {
        const val MIN_WALK_DISTANCE = 3
    }
}
