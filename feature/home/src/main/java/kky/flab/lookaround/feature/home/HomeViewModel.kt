package kky.flab.lookaround.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.ConfigRepository
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.WeatherRepository
import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Config
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.SummaryUiState
import kky.flab.lookaround.feature.home.model.UiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
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

    private val _summaryFilter: MutableStateFlow<SummaryFilter> =
        MutableStateFlow(SummaryFilter.MONTH)

    private var cachedConfig: Config = Config.Default

    val config = configRepository.configFlow

    init {
        recordRepository.flowRecords()
            .catch {
                _effect.tryEmit(
                    Effect.Error(
                        message = it.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                )
            }.launchIn(viewModelScope)

        configRepository.configFlow.onEach { config ->
            cachedConfig = config
            _state.update { value ->
                value.copy(initializedConfig = true)
            }
        }.launchIn(viewModelScope)

        recordRepository.recordingFlow.onEach { recording ->
            if (recording) {
                _effect.tryEmit(Effect.StartRecordingService)
            }

            _state.update { it.copy(recording = recording) }
        }.launchIn(viewModelScope)

        _summaryFilter
            .flatMapLatest { recordRepository.flowSummary(it) }
            .flowOn(Dispatchers.Default)
            .onEach { summary ->
                _state.update { prev ->
                    prev.copy(
                        summaryUiState =
                        if (summary.count == 0) SummaryUiState.Empty
                        else SummaryUiState.Result(summary)
                    )
                }
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

    fun updateRequestedFineLocation() {
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

    fun changeSummaryFilter(filter: SummaryFilter) {
        _state.update { value -> value.copy(summaryFilter = filter) }
        _summaryFilter.value = filter
    }

    fun onDenyPermissionForWeather() {
        _state.update {
            it.copy(
                weatherUiState = WeatherUiState.Fail("권한을 허용해주세요.")
            )
        }
    }
}
