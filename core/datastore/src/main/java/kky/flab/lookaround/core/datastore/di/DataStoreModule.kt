package kky.flab.lookaround.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    private val Context.configStore by preferencesDataStore("config")

    @Singleton
    @Provides
    fun providesConfigDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.configStore
}