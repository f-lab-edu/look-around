package kky.flab.lookaround.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kky.flab.lookaround.core.database.dao.RecordDao
import kky.flab.lookaround.core.database.model.RecordEntity

@Database(entities = [RecordEntity::class], version = 1)
@TypeConverters(RoomConverter::class)
internal abstract class AppDatabase: RoomDatabase() {
    abstract fun recordDao(): RecordDao
}