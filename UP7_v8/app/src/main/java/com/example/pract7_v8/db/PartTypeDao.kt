package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.PartType
import kotlinx.coroutines.flow.Flow

@Dao
interface PartTypeDao {

    // Для Flow (если нужно наблюдать за изменениями)
    @Query("SELECT * FROM part_types")
    fun getAllPartTypes(): Flow<List<PartType>>

    // ✅ ДОБАВЬ ЭТОТ МЕТОД (возвращает обычный список)
    @Query("SELECT * FROM part_types")
    suspend fun getAllPartTypesList(): List<PartType>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartType(partType: PartType): Long

    @Update
    suspend fun updatePartType(partType: PartType)

    @Delete
    suspend fun deletePartType(partType: PartType)

    @Query("SELECT * FROM part_types WHERE id = :id")
    suspend fun getPartTypeById(id: Int): PartType?
}