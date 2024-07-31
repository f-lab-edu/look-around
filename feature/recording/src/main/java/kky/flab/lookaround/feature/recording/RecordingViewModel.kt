package kky.flab.lookaround.feature.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.feature.recording.model.RecordingEffect
import kky.flab.lookaround.feature.recording.model.RecordingUiState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    val state: StateFlow<RecordingUiState> = recordRepository
        .recordingState
        .map { record ->
            RecordingUiState(
                path = record.path.map { LatLng(it.latitude, it.longitude) },
                distance = record.distance
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            RecordingUiState.EMPTY
        )

    val timer: Flow<Long> = flow {
        val first = recordRepository.recordingState.first()
        while (true) {
            emit(System.currentTimeMillis() - first.startTimeStamp)
            delay(1000)
        }
    }

    private val _effect: MutableSharedFlow<RecordingEffect> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effect: SharedFlow<RecordingEffect> = _effect.asSharedFlow()

    fun complete() {
        viewModelScope.launch {
            val id = recordRepository.endRecording()
            _effect.tryEmit(RecordingEffect.SavedRecording(id))
        }
    }
}