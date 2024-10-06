package kky.flab.lookaround.feature.recording.model

import kky.flab.lookaround.core.domain.model.Record

sealed interface ModifyRecordUiState {
    data object Empty : ModifyRecordUiState

    data class Result(val record: Record) : ModifyRecordUiState
}

