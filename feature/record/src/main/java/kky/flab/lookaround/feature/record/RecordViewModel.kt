package kky.flab.lookaround.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.feature.record.model.RecordUiModel
import kky.flab.lookaround.feature.record.model.RecordUiState
import kky.flab.lookaround.feature.record.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _state: MutableStateFlow<RecordUiState> = MutableStateFlow(RecordUiState.Loading)
    val state: StateFlow<RecordUiState> = _state.asStateFlow()

    init {
        recordRepository.getRecords()
            .map { record -> record.map { it.toUiModel() } }
            .onEach { _state.value = RecordUiState.Result(records = it) }
            .launchIn(viewModelScope)
    }

    fun delete(record: RecordUiModel) {
        viewModelScope.launch {
            recordRepository.deleteRecord(record.toDomain())
        }
    }
}
