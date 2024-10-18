package kky.flab.lookaround.feature.recording

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.ConfigRepository
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Config
import kky.flab.lookaround.feature.recording.model.ModifyRecordEffect
import kky.flab.lookaround.feature.recording.model.ModifyRecordUiState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyRecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository,
    configRepository: ConfigRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ModifyRecordUiState> =
        MutableStateFlow(ModifyRecordUiState.Empty)
    val state: StateFlow<ModifyRecordUiState> = _state.asStateFlow()

    private val _effect: MutableSharedFlow<ModifyRecordEffect> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<ModifyRecordEffect> = _effect.asSharedFlow()

    val config: Flow<Config> = configRepository.configFlow

    fun getRecord(id: Long) {
        viewModelScope.launch {
            val record = recordRepository.getRecord(id)
            _state.value = ModifyRecordUiState.Result(record = record)
        }
    }

    fun save(memo: String, imagePath: String?) {
        if (_state.value !is ModifyRecordUiState.Result) {
            return
        }

        val state = _state.value as ModifyRecordUiState.Result
        val update = state.record.copy(
            memo = memo,
            image = imagePath ?: ""
        )

        viewModelScope.launch {
            runCatching {
                recordRepository.updateRecord(update)
            }.onFailure {
                _effect.tryEmit(ModifyRecordEffect.Error("오류가 발생했습니다."))
            }.onSuccess {
                _effect.tryEmit(ModifyRecordEffect.SaveRecord)
            }
        }
    }
}
