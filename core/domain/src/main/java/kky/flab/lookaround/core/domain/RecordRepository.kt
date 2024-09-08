package kky.flab.lookaround.core.domain

import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.domain.model.Summary
import kotlinx.coroutines.flow.Flow

interface RecordRepository {

    val recording: Flow<Boolean>

    val recordingState: Flow<Record>

    fun getRecords(): Flow<List<Record>>

    suspend fun getRecord(id: Long): Record

    suspend fun saveRecord(record: Record): Long

    suspend fun updateRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    fun startRecording()

    suspend fun endRecording(): Long

    fun addPath(path: Path)

    fun getSummary(filter: SummaryFilter): Flow<Summary>
}