package kky.flab.lookaround.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kky.flab.lookaround.core.database.model.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM record ORDER BY id DESC")
    fun getRecord(): Flow<List<RecordEntity>>

    @Insert
    suspend fun insertRecord(record: RecordEntity): Long

    @Delete
    suspend fun deleteRecord(record: RecordEntity)

    @Update
    suspend fun updateRecord(record: RecordEntity)
}