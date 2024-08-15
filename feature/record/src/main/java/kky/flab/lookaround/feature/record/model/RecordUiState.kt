package kky.flab.lookaround.feature.record.model

import kky.flab.lookaround.core.domain.model.Record

sealed interface RecordUiState {
    data object Loading: RecordUiState

    data class Result(
        val records: List<Record>
    ): RecordUiState
}
