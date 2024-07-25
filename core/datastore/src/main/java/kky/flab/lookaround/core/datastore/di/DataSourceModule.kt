package kky.flab.lookaround.core.datastore.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kky.flab.lookaround.core.datastore.ConfigDataSource
import kky.flab.lookaround.core.datastore.ConfigDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DataSourceModule {
    @Singleton
    @Binds
    fun bindsConfigDataSource(
        configDataSourceImpl: ConfigDataSourceImpl
    ): ConfigDataSource
}
