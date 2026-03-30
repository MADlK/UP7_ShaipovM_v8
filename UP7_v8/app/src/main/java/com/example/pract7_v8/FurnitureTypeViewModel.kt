package com.example.pract7_v8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract7_v8.db.FurnitureType
import com.example.pract7_v8.FurnitureTypeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FurnitureTypeViewModel(private val repository: FurnitureTypeRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    val allFurnitureTypes: LiveData<List<FurnitureType>> =
        _searchQuery.switchMap { query ->
            if (query.isNullOrBlank()) {
                repository.getAllFurnitureTypes().asLiveData()
            } else {
                repository.searchFurnitureTypes(query).asLiveData()
            }
        }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun addFurnitureType(name: String, description: String?, basePrice: Double, imageUrl: String?) {
        viewModelScope.launch {
            repository.insertFurnitureType(
                FurnitureType(0, name, description, basePrice, imageUrl)
            )
        }
    }

    fun updateFurnitureType(id: Int, name: String, description: String?, basePrice: Double, imageUrl: String?) {
        viewModelScope.launch {
            repository.updateFurnitureType(
                FurnitureType(id, name, description, basePrice, imageUrl)
            )
        }
    }

    fun deleteFurnitureType(ft: FurnitureType) {
        viewModelScope.launch {
            repository.deleteFurnitureType(ft)
        }
    }

    fun deleteFurnitureTypeById(id: Int) {
        viewModelScope.launch {
            repository.deleteFurnitureTypeById(id)
        }
    }
}