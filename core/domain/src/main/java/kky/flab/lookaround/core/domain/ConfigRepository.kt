package kky.flab.lookaround.core.domain

import kky.flab.lookaround.core.domain.model.Config
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    val configFlow: Flow<Config>

    suspend fun updateConfig(config: Config)
}
