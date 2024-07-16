package kky.flab.lookaround.core.domain

import kotlinx.coroutines.flow.Flow
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow

interface RecordRepository {

    val recording: Flow<Boolean>

    fun getRecords(): Flow<List<Record>>

    suspend fun saveRecord(record: Record): Long

    suspend fun updateRecord(record: Record)

    suspend fun deleteRecord(record: Record)

    fun startRecording()

    fun endRecording()
}