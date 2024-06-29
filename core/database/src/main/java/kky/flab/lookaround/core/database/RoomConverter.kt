package kky.flab.lookaround.core.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kky.flab.lookaround.core.database.model.PathEntity

@ProvidedTypeConverter
class RoomConverter(
    private val moshi: Moshi
) {
    @TypeConverter
    fun pathToJson(path: List<PathEntity>): String {
        val listType = Types.newParameterizedType(List::class.java, PathEntity::class.java)
        val adapter: JsonAdapter<List<PathEntity>> = moshi.adapter(listType)
        return adapter.toJson(path)
    }

    @TypeConverter
    fun jsonToPath(json: String): List<PathEntity> {
        val listType = Types.newParameterizedType(List::class.java, PathEntity::class.java)
        val adapter: JsonAdapter<List<PathEntity>> = moshi.adapter(listType)
        return adapter.fromJson(json) ?: throw IllegalArgumentException("잘못된 JSON 정보입니다.")
    }
}