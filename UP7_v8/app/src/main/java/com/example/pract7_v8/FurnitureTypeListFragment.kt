package com.example.pract7_v8

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.FurnitureType
import com.example.pract7_v8.databinding.FragmentFurnitureTypeListBinding


import android.widget.Toast

import androidx.lifecycle.lifecycleScope


import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

import android.text.TextWatcher

import androidx.fragment.app.viewModels


import androidx.recyclerview.widget.LinearLayoutManager

import com.example.pract7_v8.FurnitureTypeRepository

import com.example.pract7_v8.MainActivity
import com.example.pract7_v8.FurnitureTypeViewModel
import com.example.pract7_v8.FurnitureTypeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

import kotlinx.coroutines.withContext


import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.pract7_v8.R

import com.example.pract7_v8.CartManager





class FurnitureTypeListFragment : Fragment() {

    private var _binding: FragmentFurnitureTypeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FurnitureTypeViewModel by viewModels {
        FurnitureTypeViewModelFactory(
            FurnitureTypeRepository(AppDatabase.getDatabase(requireContext()).furnitureTypeDao())
        )
    }

    private lateinit var adapter: FurnitureTypeAdapter
    private var isWorker: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFurnitureTypeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        // Определяем роль пользователя
        val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
        val userRole = prefs.getString("user_role", "")
        isWorker = (userRole == "WORKER")

        // Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // ✅ Кнопка корзины (только для клиента)
        binding.btnCart.visibility = if (isWorker) View.GONE else View.VISIBLE
        binding.btnCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }



        // Поиск
        setupSearch()

        // ✅ RecyclerView с разными кнопками для роли
        adapter = if (isWorker) {
            // Режим работника: редактирование и удаление
            FurnitureTypeAdapter(
                onEditClick = { ft ->
                    val bundle = Bundle()
                    bundle.putInt("furnitureTypeId", ft.id)
                    findNavController().navigate(R.id.furnitureTypeEditorFragment, bundle)
                },
                onDeleteClick = { ft -> deleteFurnitureType(ft) },
                onAddToCart = null
            )
        } else {
            // Режим клиента: только корзина
            FurnitureTypeAdapter(
                onEditClick = null,
                onDeleteClick = null,
                onAddToCart = { ft ->
                    CartManager.addItem(ft)
                    Toast.makeText(requireContext(), "✅ ${ft.name} в корзине", Toast.LENGTH_SHORT).show()
                    updateCartBadge()
                }
            )
        }

        binding.recyclerViewFurniture.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFurniture.adapter = adapter

        // Наблюдение за данными
        viewModel.allFurnitureTypes.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

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
                        viewModel.search(s.toString().trim())
                    }
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun deleteFurnitureType(ft: com.example.pract7_v8.db.FurnitureType) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.deleteFurnitureType(ft)
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "🗑️ Удалено", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCartBadge() {
        val count = CartManager.getCount()
        if (count > 0) {
            binding.btnCart.text = "🛒 $count"
        } else {
            binding.btnCart.text = "🛒 Корзина"
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isWorker) {
            updateCartBadge()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}