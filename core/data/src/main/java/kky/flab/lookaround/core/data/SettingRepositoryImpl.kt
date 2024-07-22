package kky.flab.lookaround.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kky.flab.lookaround.core.domain.SettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SettingRepositoryImpl @Inject constructor(
    private val configStore: DataStore<Preferences>,
) : SettingRepository {
    object PreferencesKey {
        val REQUESTED_FINE_LOCATION = booleanPreferencesKey("isRequestedFineLocation")
        val REQUESTED_BACKGROUND_LOCATION = booleanPreferencesKey("isRequestedBackgroundLocation")
        val REQUESTED_READ_STORAGE = booleanPreferencesKey("isRequestedReadStorage")
        val DARK_THEME = booleanPreferencesKey("isDarkTheme")
    }

    override val settingFlow: Flow<kky.flab.lookaround.core.domain.model.Setting> =
        configStore.data.map { preferences ->
            kky.flab.lookaround.core.domain.model.Setting(
                requestFineLocation = preferences[PreferencesKey.REQUESTED_FINE_LOCATION] ?: false,
                requestBackgroundLocation = preferences[PreferencesKey.REQUESTED_BACKGROUND_LOCATION]
                    ?: false,
                requestReadStorage = preferences[PreferencesKey.REQUESTED_READ_STORAGE] ?: false,
                darkTheme = preferences[PreferencesKey.DARK_THEME] ?: false
            )
        }

    override suspend fun updateConfig(setting: kky.flab.lookaround.core.domain.model.Setting) {
        configStore.edit { preferences ->
            preferences[PreferencesKey.REQUESTED_FINE_LOCATION] = setting.requestFineLocation
            preferences[PreferencesKey.REQUESTED_BACKGROUND_LOCATION] =
                setting.requestBackgroundLocation
            preferences[PreferencesKey.REQUESTED_READ_STORAGE] = setting.requestReadStorage
            preferences[PreferencesKey.DARK_THEME] = setting.darkTheme
        }
    }
}