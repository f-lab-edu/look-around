package kky.flab.lookaround.core.database.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kky.flab.lookaround.core.database.AppDatabase
import kky.flab.lookaround.core.database.RoomConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
            .addTypeConverter(RoomConverter(moshi))
            .build()

        return db
    }
}