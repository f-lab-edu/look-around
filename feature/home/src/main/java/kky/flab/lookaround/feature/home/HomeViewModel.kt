package kky.flab.lookaround.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kky.flab.lookaround.feature.home.model.Result
import kky.flab.lookaround.feature.home.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class HomeViewModel: ViewModel() {
    private val _state = MutableStateFlow<UiState>(Result(start = false))
    val state:StateFlow<UiState> = _state.asStateFlow()
    var start: Boolean = false
        private set(value: Boolean) {
            field = value
        }

    fun toggleWalk() {
        if (state.value !is Result) {
            return
        }

        val currentState = state.value as Result

        viewModelScope.launch {
            _state.value = currentState.copy(start = !currentState.start)
        }
    }
}