package kky.flab.lookaround.feature.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kky.flab.lookaround.core.domain.ConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    configRepository: ConfigRepository
) : ViewModel() {
    val darkThemeState: Flow<Boolean> = configRepository.configFlow.map { it.darkTheme }
}
