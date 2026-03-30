package com.example.pract7_v8

import com.example.pract7_v8.db.SupplierDao
import com.example.pract7_v8.db.Supplier
import kotlinx.coroutines.flow.Flow

class SupplierRepository(private val supplierDao: SupplierDao) {

    fun getAllSuppliers(): Flow<List<Supplier>> {
        return supplierDao.getAllSuppliers()
    }

    // ✅ ДОБАВЛЕНО: Поиск
    fun searchSuppliers(query: String): Flow<List<Supplier>> {
        return supplierDao.searchSuppliers(query)
    }

    suspend fun getSupplierById(id: Int): Supplier? {
        return supplierDao.getSupplierById(id)
    }

    suspend fun insertSupplier(supplier: Supplier): Long {
        return supplierDao.insertSupplier(supplier)
    }

    suspend fun updateSupplier(supplier: Supplier) {
        supplierDao.updateSupplier(supplier)
    }

    suspend fun deleteSupplier(supplier: Supplier) {
        supplierDao.deleteSupplier(supplier)
    }
}