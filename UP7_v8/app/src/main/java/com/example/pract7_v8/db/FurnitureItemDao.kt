package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.FurnitureItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FurnitureItemDao {
    @Query("SELECT * FROM furniture_items")
    fun getAllFurnitureItems(): Flow<List<FurnitureItem>>

    @Query("SELECT * FROM furniture_items WHERE typeId = :typeId")
    fun getFurnitureItemsByType(typeId: Int): Flow<List<FurnitureItem>>

    @Query("SELECT * FROM furniture_items WHERE id = :id")
    suspend fun getFurnitureItemById(id: Int): FurnitureItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFurnitureItem(item: FurnitureItem): Long

    @Update
    suspend fun updateFurnitureItem(item: FurnitureItem)

    @Delete
    suspend fun deleteFurnitureItem(item: FurnitureItem)

    @Query("DELETE FROM furniture_items WHERE id = :id")
    suspend fun deleteFurnitureItemById(id: Int)
}