package com.example.pract7_v8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SupplierViewModelFactory(private val repository: SupplierRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupplierViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SupplierViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}