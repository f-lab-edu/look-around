package kky.flab.lookaround.core.datastore

import kky.flab.lookaround.core.domain.model.Config
import kotlinx.coroutines.flow.Flow

interface ConfigDataSource {
    val config: Flow<Config>

    suspend fun updateConfig(config: Config)
}