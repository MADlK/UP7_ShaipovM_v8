package com.example.pract7_v8

import com.example.pract7_v8.db.FurnitureTypeDao
import com.example.pract7_v8.db.FurnitureType
import kotlinx.coroutines.flow.Flow

class FurnitureTypeRepository(private val dao: FurnitureTypeDao) {

    fun getAllFurnitureTypes(): Flow<List<FurnitureType>> = dao.getAllFurnitureTypes()

    fun searchFurnitureTypes(query: String): Flow<List<FurnitureType>> = dao.searchFurnitureTypes(query)

    suspend fun getFurnitureTypeById(id: Int): FurnitureType? = dao.getFurnitureTypeById(id)

    suspend fun insertFurnitureType(ft: FurnitureType): Long = dao.insertFurnitureType(ft)

    suspend fun updateFurnitureType(ft: FurnitureType) = dao.updateFurnitureType(ft)

    suspend fun deleteFurnitureType(ft: FurnitureType) = dao.deleteFurnitureType(ft)

    suspend fun deleteFurnitureTypeById(id: Int) = dao.deleteFurnitureTypeById(id)
}