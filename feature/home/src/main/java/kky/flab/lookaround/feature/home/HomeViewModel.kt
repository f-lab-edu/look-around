package kky.flab.lookaround.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.Error
import kky.flab.lookaround.feature.home.model.Loading
import kky.flab.lookaround.feature.home.model.Result
import kky.flab.lookaround.feature.home.model.ShowEndRecordingMessage
import kky.flab.lookaround.feature.home.model.ShowStartRecordingMessage
import kky.flab.lookaround.feature.home.model.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
) : ViewModel() {

    private val _state:MutableStateFlow<UiState> = MutableStateFlow(Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        recordRepository.getRecords()
            .flatMapLatest {
                recordRepository.recording
                    .map {
                        Log.d("HomeViewModel", it.toString())
                        Result(
                            recording = it
                        )
                    }
            }
            .catch {
                _effect.tryEmit(
                    Error(
                        message = it.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                )
            }.onEach {
                _state.value = it
            }.launchIn(viewModelScope)
    }

    fun showRecordingMessage() {
        if (state.value !is Result) {
            return
        }

        val current = state.value as Result

        if (current.recording) {
            _effect.tryEmit(ShowEndRecordingMessage())
        } else {
            _effect.tryEmit(ShowStartRecordingMessage())
        }
    }

    fun toggleRecording() {
        if (state.value !is Result) {
            return
        }

        val current = state.value as Result

        if (current.recording) {
            recordRepository.endRecording()
        } else {
            recordRepository.startRecording()
        }
    }
}