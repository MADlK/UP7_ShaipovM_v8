package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.FurnitureType
import kotlinx.coroutines.flow.Flow

@Dao
interface FurnitureTypeDao {

    @Query("SELECT * FROM furniture_types ORDER BY name")
    fun getAllFurnitureTypes(): Flow<List<FurnitureType>>

    @Query("SELECT * FROM furniture_types WHERE id = :id")
    suspend fun getFurnitureTypeById(id: Int): FurnitureType?

    @Query("""
        SELECT * FROM furniture_types 
        WHERE name LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
    """)
    fun searchFurnitureTypes(query: String): Flow<List<FurnitureType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFurnitureType(furnitureType: FurnitureType): Long

    @Update
    suspend fun updateFurnitureType(furnitureType: FurnitureType)

    @Delete
    suspend fun deleteFurnitureType(furnitureType: FurnitureType)

    @Query("DELETE FROM furniture_types WHERE id = :id")
    suspend fun deleteFurnitureTypeById(id: Int)
}