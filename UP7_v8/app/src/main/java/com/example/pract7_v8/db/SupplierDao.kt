package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Query("SELECT * FROM suppliers")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getSupplierById(id: Int): Supplier?


    @Query("""
        SELECT * FROM suppliers 
        WHERE name LIKE '%' || :query || '%' 
        OR email LIKE '%' || :query || '%' 
        OR phone LIKE '%' || :query || '%' 
        OR contactPerson LIKE '%' || :query || '%'
    """)
    fun searchSuppliers(query: String): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    @Query("DELETE FROM suppliers WHERE id = :id")
    suspend fun deleteSupplierById(id: Int)
}