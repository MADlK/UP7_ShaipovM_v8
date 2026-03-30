package com.example.pract7_v8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pract7_v8.FurnitureTypeRepository

class FurnitureTypeViewModelFactory(
    private val repository: FurnitureTypeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FurnitureTypeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FurnitureTypeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}