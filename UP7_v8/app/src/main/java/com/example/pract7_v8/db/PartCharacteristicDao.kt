package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.PartCharacteristic
import kotlinx.coroutines.flow.Flow

@Dao
interface PartCharacteristicDao {

    @Query("SELECT * FROM part_characteristics")  // ✅ Нет weight!
    fun getAllPartCharacteristics(): Flow<List<PartCharacteristic>>

    @Query("SELECT * FROM part_characteristics WHERE id = :id")
    suspend fun getPartCharacteristicById(id: Int): PartCharacteristic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartCharacteristic(partCharacteristic: PartCharacteristic): Long

    @Update
    suspend fun updatePartCharacteristic(partCharacteristic: PartCharacteristic)

    @Delete
    suspend fun deletePartCharacteristic(partCharacteristic: PartCharacteristic)
}