package kky.flab.lookaround.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.ConfigRepository
import kky.flab.lookaround.core.domain.model.Config
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val configRepository: ConfigRepository
) : ViewModel() {

    val configState: StateFlow<Config> = configRepository.configFlow
        .stateIn(
            initialValue = Config.Default,
            scope = viewModelScope,
            started = SharingStarted.Eagerly
        )

    fun setDarkMode(enable: Boolean) {
        viewModelScope.launch {
            configRepository.updateConfig(
                configState.value.copy(
                    darkTheme = enable
                )
            )
        }
    }
}
