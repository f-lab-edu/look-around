package kky.flab.lookaround.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kky.flab.lookaround.core.database.AppDatabase
import kky.flab.lookaround.core.database.dao.RecordDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Singleton
    @Provides
    fun bindsRecordDao(
        db: AppDatabase
    ): RecordDao = db.recordDao()
}