package kky.flab.lookaround.core.data

import kky.flab.lookaround.core.datastore.ConfigDataSource
import kky.flab.lookaround.core.domain.ConfigRepository
import kky.flab.lookaround.core.domain.model.Config
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ConfigRepositoryImpl @Inject constructor(
    private val configDataSource: ConfigDataSource,
) : ConfigRepository {
    override val configFlow: Flow<Config> = configDataSource.config

    override suspend fun updateConfig(config: Config) {
        configDataSource.updateConfig(config)
    }
}
