package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.SupplierPart
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierPartDao {

    @Query("SELECT * FROM supplier_parts")
    fun getAllSupplierParts(): Flow<List<SupplierPart>>

    // ✅ МЕТОД: получить детали поставщика по ID
    @Query("SELECT * FROM supplier_parts WHERE supplierId = :supplierId")
    suspend fun getPartsBySupplierId(supplierId: Int): List<SupplierPart>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplierPart(supplierPart: SupplierPart): Long

    @Update
    suspend fun updateSupplierPart(supplierPart: SupplierPart)

    @Delete
    suspend fun deleteSupplierPart(supplierPart: SupplierPart)
}