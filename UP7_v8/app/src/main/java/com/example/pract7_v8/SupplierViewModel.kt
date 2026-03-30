package com.example.pract7_v8

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract7_v8.db.Supplier
import com.example.pract7_v8.SupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


import kotlinx.coroutines.flow.collectLatest


class SupplierViewModel(private val repository: SupplierRepository) : ViewModel() {

    // ✅ Вариант 1: Используем Flow (проще)
    private val _suppliers = MutableLiveData<List<Supplier>>()
    val suppliers: LiveData<List<Supplier>> = _suppliers

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    init {
        // ✅ Загружаем данные при создании ViewModel
        loadSuppliers("")
    }

    // ✅ Загрузка списка поставщиков
    fun loadSuppliers(query: String) {
        viewModelScope.launch {  // ✅ viewModelScope, не lifecycleScope!
            try {
                val flow = if (query.isEmpty()) {
                    repository.getAllSuppliers()
                } else {
                    repository.searchSuppliers(query)
                }

                flow.collectLatest { suppliers ->
                    _suppliers.value = suppliers
                }
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }

    // ✅ Поиск
    fun search(query: String) {
        _searchQuery.value = query
        loadSuppliers(query)
    }

    // ✅ Добавление поставщика
    fun addSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.insertSupplier(supplier)
            // Список обновится автоматически через Flow
        }
    }

    // ✅ Удаление поставщика
    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
            // Список обновится автоматически через Flow
        }
    }

    // ✅ Обновление поставщика
    fun updateSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.updateSupplier(supplier)
        }
    }
}