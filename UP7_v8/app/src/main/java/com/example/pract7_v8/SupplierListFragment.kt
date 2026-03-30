package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.Supplier
import com.example.pract7_v8.databinding.FragmentSupplierListBinding

import kotlinx.coroutines.Job
import android.widget.Toast

import androidx.lifecycle.lifecycleScope


import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch







import androidx.navigation.fragment.findNavController



import com.example.pract7_v8.databinding.ItemSupplierBinding
import com.example.pract7_v8.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.withContext


import android.text.TextWatcher




import kotlinx.coroutines.CancellationException


import kotlinx.coroutines.isActive


class SupplierListFragment : Fragment() {

    private var _binding: FragmentSupplierListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SupplierAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        // Кнопка назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Кнопка "+"
        binding.fabAddSupplier.setOnClickListener {
            if (isAdded && _binding != null) {
                AddSupplierDialogFragment().show(parentFragmentManager, "AddSupplierDialog")
            }
        }

        // Поиск
        setupSearch()

        // RecyclerView
        adapter = SupplierAdapter { supplier -> deleteSupplier(supplier) }
        binding.recyclerViewSuppliers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSuppliers.adapter = adapter

        // Загрузка данных
        loadSuppliers("")

        // Выход
        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    if (isActive && isAdded) {
                        loadSuppliers(s.toString().trim())
                    }
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun loadSuppliers(query: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (!isActive || !isAdded) return@launch

                val db = AppDatabase.getDatabase(requireContext())
                val suppliersFlow = if (query.isEmpty()) {
                    db.supplierDao().getAllSuppliers()
                } else {
                    db.supplierDao().searchSuppliers(query)
                }

                suppliersFlow.collectLatest { suppliers ->
                    // ✅ Проверка перед обновлением UI
                    if (!isActive || !isAdded || _binding == null) return@collectLatest
                    adapter.submitList(suppliers)
                }

            } catch (e: CancellationException) {
                // ✅ Нормальная отмена — игнорируем
            } catch (e: Exception) {
                // ✅ Показываем ошибку только если фрагмент активен
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun refreshSuppliers() {
        if (!isAdded || _binding == null) return
        val query = binding.etSearch.text.toString().trim()
        loadSuppliers(query)
    }

    private fun deleteSupplier(supplier: Supplier) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                withContext(Dispatchers.IO) {
                    db.supplierDao().deleteSupplier(supplier)
                }
                // ✅ Проверка перед Toast
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "🗑️ Удалён", Toast.LENGTH_SHORT).show()
                    refreshSuppliers()
                }
            } catch (e: CancellationException) {
                // Игнорируем отмену
            } catch (e: Exception) {
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}