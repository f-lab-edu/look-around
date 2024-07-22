package kky.flab.lookaround.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.model.Setting
import kky.flab.lookaround.core.domain.SettingRepository
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.WeatherRepository
import kky.flab.lookaround.core.ui.util.getAddress
import kky.flab.lookaround.core.ui.util.xlsx.XlsxParser
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.Error
import kky.flab.lookaround.feature.home.model.RecordUiState
import kky.flab.lookaround.feature.home.model.ShowEndRecordingMessage
import kky.flab.lookaround.feature.home.model.ShowStartRecordingMessage
import kky.flab.lookaround.feature.home.model.UiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    private val weatherRepository: WeatherRepository,
    private val settingRepository: SettingRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.EMPTY)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    private lateinit var cachedSetting: Setting

    val setting = settingRepository.settingFlow

    init {
        recordRepository.getRecords()
            .catch {
                _effect.tryEmit(
                    Error(
                        message = it.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                )
            }.onEach {
                _state.value = _state.value.copy(
                    recordState = RecordUiState.Result(it)
                )
            }.launchIn(viewModelScope)

        settingRepository.settingFlow.onEach {
            cachedSetting = it
        }.launchIn(viewModelScope)

        recordRepository.recording.onEach {
            _state.value = _state.value.copy(
                recording = it
            )
        }.launchIn(viewModelScope)
    }

    fun showRecordingMessage() {
        val recording = state.value.recording

        if (recording) {
            _effect.tryEmit(ShowEndRecordingMessage())
        } else {
            _effect.tryEmit(ShowStartRecordingMessage())
        }
    }

    fun toggleRecording() {
        val recording = state.value.recording

        if (recording) {
            recordRepository.endRecording()
        } else {
            recordRepository.startRecording()
        }
    }

    fun updateRequestedFinLocation() {
        viewModelScope.launch {
            settingRepository.updateConfig(
                cachedSetting.copy(
                    requestFineLocation = true
                )
            )
        }
    }

    fun loadWeather(context: Context) {
        viewModelScope.launch {
            runCatching {
                val address = getAddress(context) ?: return@launch
                val parseResult = withContext(Dispatchers.IO) {
                    XlsxParser.findXY(context, address)
                }

                weatherRepository.getRealTimeWeather(parseResult.nx, parseResult.ny)
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
