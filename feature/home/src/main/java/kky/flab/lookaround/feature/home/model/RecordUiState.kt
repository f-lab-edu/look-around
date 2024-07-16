package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.model.Record
import kky.flab.lookaround.core.domain.model.Weather

sealed interface RecordUiState {
    data object Loading : RecordUiState

    data class Result(val data: List<Record>) : RecordUiState
}