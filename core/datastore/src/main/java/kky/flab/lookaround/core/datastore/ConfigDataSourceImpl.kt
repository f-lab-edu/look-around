package kky.flab.lookaround.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kky.flab.lookaround.core.domain.model.Config
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ConfigDataSourceImpl @Inject constructor(
    private val configStore: DataStore<Preferences>
): ConfigDataSource {
    object PreferencesKey {
        val REQUESTED_FINE_LOCATION = booleanPreferencesKey("isRequestedFineLocation")
        val REQUESTED_BACKGROUND_LOCATION = booleanPreferencesKey("isRequestedBackgroundLocation")
        val REQUESTED_READ_STORAGE = booleanPreferencesKey("isRequestedReadStorage")
        val DARK_THEME = booleanPreferencesKey("isDarkTheme")
    }

    override val config: Flow<Config>
        get() = configStore.data.map { preferences ->
            Config(
                requestFineLocation = preferences[PreferencesKey.REQUESTED_FINE_LOCATION] ?: false,
                requestBackgroundLocation = preferences[PreferencesKey.REQUESTED_BACKGROUND_LOCATION] ?: false,
                requestReadStorage = preferences[PreferencesKey.REQUESTED_READ_STORAGE] ?: false,
                darkTheme = preferences[PreferencesKey.DARK_THEME] ?: false
            )
        }

    override suspend fun updateConfig(config: Config) {
        configStore.edit { preferences ->
            preferences[PreferencesKey.REQUESTED_FINE_LOCATION] = config.requestFineLocation
            preferences[PreferencesKey.REQUESTED_BACKGROUND_LOCATION] =
                config.requestBackgroundLocation
            preferences[PreferencesKey.REQUESTED_READ_STORAGE] = config.requestReadStorage
            preferences[PreferencesKey.DARK_THEME] = config.darkTheme
        }
    }
}
