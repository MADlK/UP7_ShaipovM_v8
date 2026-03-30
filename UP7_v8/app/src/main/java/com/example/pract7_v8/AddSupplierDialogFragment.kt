package com.example.pract7_v8

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.Supplier
import com.example.pract7_v8.databinding.FragmentAddSupplierDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddSupplierDialogFragment : DialogFragment() {

    private var _binding: FragmentAddSupplierDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSupplierDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            saveSupplier()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveSupplier() {
        val name = binding.etSupplierName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val contactPerson = binding.etContactPerson.text.toString().trim()

        if (name.isEmpty()) {
            binding.etSupplierName.error = "Введите название"
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Введите email"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val supplier = Supplier(
                    0,
                    name,
                    email,
                    phone,
                    address,
                    contactPerson
                )

                val id = db.supplierDao().insertSupplier(supplier)

                withContext(Dispatchers.Main) {
                    if (id > 0) {
                        Toast.makeText(requireContext(), "✅ Поставщик добавлен", Toast.LENGTH_SHORT).show()
                        dismiss()
                        // Обновляем список
                        (parentFragment as? SupplierListFragment)?.refreshSuppliers()
                    } else {
                        Toast.makeText(requireContext(), "❌ Ошибка добавления", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}