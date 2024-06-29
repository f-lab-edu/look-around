package kky.flab.lookaround.core.domain

import kotlinx.coroutines.flow.Flow
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.flow.StateFlow

interface RecordRepository {

    val recording: StateFlow<Boolean>

    fun saveRecord(record: Record): Long

    fun getRecords(
    ): Flow<List<Record>>

    fun updateRecord(
        record: Record
    )

    fun deleteRecord(
        record: Record
    )

    fun startRecording()

    fun endRecording()
}