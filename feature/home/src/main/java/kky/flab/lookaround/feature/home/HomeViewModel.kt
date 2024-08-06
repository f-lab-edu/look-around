package kky.flab.lookaround.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.ConfigRepository
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.WeatherRepository
import kky.flab.lookaround.core.domain.model.Config
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.RecordUiState
import kky.flab.lookaround.feature.home.model.UiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val weatherRepository: WeatherRepository,
    private val configRepository: ConfigRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.EMPTY)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    private lateinit var cachedConfig: Config

    val config = configRepository.configFlow

    init {
        recordRepository.getRecords()
            .catch {
                _effect.tryEmit(
                    Effect.Error(
                        message = it.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                )
            }.onEach {
                _state.value = _state.value.copy(
                    recordState = RecordUiState.Result(it)
                )
            }.launchIn(viewModelScope)

        configRepository.configFlow.onEach {
            cachedConfig = it
        }.launchIn(viewModelScope)

        recordRepository.recording.onEach { recording ->
            if (recording) {
                _effect.tryEmit(Effect.StartRecordingService)
            } else {
                _effect.tryEmit(Effect.StopRecordingService)
            }

            _state.update { it.copy(recording = recording) }
        }.launchIn(viewModelScope)
    }

    fun showRecordingMessage() {
        val recording = state.value.recording

        if (recording) {
            _effect.tryEmit(Effect.ShowEndRecordingMessage)
        } else {
            _effect.tryEmit(Effect.ShowStartRecordingMessage)
        }
    }

    fun startRecording() {
        recordRepository.startRecording()
    }

    fun updateRequestedFinLocation() {
        viewModelScope.launch {
            configRepository.updateConfig(
                cachedConfig.copy(
                    requestFineLocation = true
                )
            )
        }
    }

    fun loadWeather(nx: Int, ny: Int) {
        viewModelScope.launch {
            //재시도 클릭 시 로딩상태로 바꿈
            if (state.value.weatherUiState is WeatherUiState.Fail) {
                _state.update { it.copy(weatherUiState = WeatherUiState.Loading) }
            }

            runCatching {
                weatherRepository.getRealTimeWeather(nx, ny)
            }.onFailure {
                _state.value = _state.value.copy(
                    weatherUiState = WeatherUiState.Fail(it.message ?: "날씨를 가져오는 데 실패하였습니다.")
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    weatherUiState = WeatherUiState.Result(it)
                )
            }
        }
    }
}
