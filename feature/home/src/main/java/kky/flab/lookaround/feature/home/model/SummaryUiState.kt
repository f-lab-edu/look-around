package kky.flab.lookaround.feature.home.model

import kky.flab.lookaround.core.domain.model.Summary

sealed interface SummaryUiState {
    data object Loading : SummaryUiState

    data object Empty : SummaryUiState

    data class Result(val summary: Summary) : SummaryUiState
}