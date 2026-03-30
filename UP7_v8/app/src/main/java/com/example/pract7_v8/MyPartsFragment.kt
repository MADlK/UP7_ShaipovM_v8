package com.example.pract7_v8

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.PartCharacteristic
import com.example.pract7_v8.db.SupplierPart
import com.example.pract7_v8.databinding.FragmentMyPartsBinding
import com.example.pract7_v8.databinding.DialogAddPartBinding
import com.example.pract7_v8.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPartsFragment : Fragment() {

    private var _binding: FragmentMyPartsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyPartsAdapter
    private var supplierId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAddPart.setOnClickListener {
            showAddPartDialog()
        }

        adapter = MyPartsAdapter { supplierPart -> deletePart(supplierPart) }
        binding.recyclerViewParts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewParts.adapter = adapter

        // ✅ Берём supplierId из SharedPreferences (не из БД!)
        loadSupplierId()

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun loadSupplierId() {
        val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
        supplierId = prefs.getInt("supplier_id", -1)

        if (supplierId <= 0) {
            Toast.makeText(requireContext(), "❌ Аккаунт не привязан к поставщику", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        loadParts()
    }

    private fun loadParts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val parts = withContext(Dispatchers.IO) {
                    db.supplierPartDao().getPartsBySupplierId(supplierId)
                }

                adapter.submitList(parts ?: emptyList())

                if (parts.isNullOrEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerViewParts.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerViewParts.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddPartDialog() {
        val typeNames = arrayOf("Шурупы", "Саморезы", "Болты", "Гайки", "Другое")

        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAddPartBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerPartType.adapter = adapter

        dialogBinding.btnSave.setOnClickListener {
            val selectedIndex = dialogBinding.spinnerPartType.selectedItemPosition
            if (selectedIndex >= 0) {
                addPart(selectedIndex + 1, dialogBinding)
                dialog.dismiss()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addPart(partTypeId: Int, dialogBinding: DialogAddPartBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val price = dialogBinding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = dialogBinding.etQuantity.text.toString().toIntOrNull() ?: 0
                val leadTime = dialogBinding.etDeliveryDays.text.toString().toIntOrNull() ?: 7

                val db = AppDatabase.getDatabase(requireContext())

                val partCharId = withContext(Dispatchers.IO) {
                    db.partCharacteristicDao().insertPartCharacteristic(
                        PartCharacteristic(0, partTypeId, 0f, "", 0f, "Деталь")
                    )
                }

                withContext(Dispatchers.IO) {
                    db.supplierPartDao().insertSupplierPart(
                        SupplierPart(0, supplierId, partCharId.toInt(), price, quantity, leadTime)
                    )
                }

                Toast.makeText(requireContext(), "✅ Деталь добавлена", Toast.LENGTH_SHORT).show()
                loadParts()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deletePart(supplierPart: SupplierPart) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                withContext(Dispatchers.IO) {
                    db.supplierPartDao().deleteSupplierPart(supplierPart)
                }
                Toast.makeText(requireContext(), "🗑️ Удалено", Toast.LENGTH_SHORT).show()
                loadParts()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}