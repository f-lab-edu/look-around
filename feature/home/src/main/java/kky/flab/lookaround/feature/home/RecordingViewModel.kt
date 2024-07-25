package kky.flab.lookaround.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    val state: StateFlow<Record> = recordRepository
        .recordingState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Record.EMPTY
        )

    val timer: Flow<Long> = flow {
        val first = recordRepository.recordingState.first()
        while (true) {
            emit(System.currentTimeMillis() - first.startTimeStamp)
            delay(1000)
        }
    }

    fun complete() {
        viewModelScope.launch {
            recordRepository.endRecording()
        }
    }
}