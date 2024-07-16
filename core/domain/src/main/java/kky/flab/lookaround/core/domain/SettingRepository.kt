package kky.flab.lookaround.core.domain

import kky.flab.lookaround.core.domain.model.Setting
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    val settingFlow: Flow<Setting>

    suspend fun updateConfig(setting: Setting)
}
