package kky.flab.lookaround.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kky.flab.lookaround.core.data.RecordRepositoryImpl
import kky.flab.lookaround.core.data.WeatherRepositoryImpl
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Singleton
    @Binds
    fun getRecordRepository(
        recordRepository: RecordRepositoryImpl
    ): RecordRepository

    @Singleton
    @Binds
    fun getWeatherRepository(
        weatherRepository: WeatherRepositoryImpl
    ): WeatherRepository
}