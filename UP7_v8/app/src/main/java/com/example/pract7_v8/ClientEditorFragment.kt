package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.User
import com.example.pract7_v8.databinding.FragmentClientEditorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientEditorFragment : Fragment() {

    private var _binding: FragmentClientEditorBinding? = null
    private val binding get() = _binding!!

    // ✅ Читаем ID клиента из аргументов
    private val clientId: Int by lazy {
        arguments?.getInt("clientId", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        // Кнопка назад
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Заголовок
        if (clientId > 0) {
            binding.toolbar.title = "✏️ Редактировать скидку"
            loadClient(clientId)
        } else {
            binding.toolbar.title = "➕ Новый клиент"
        }

        // Кнопка: сохранить
        binding.btnSave.setOnClickListener {
            saveClient()
        }

        // Кнопка: отмена
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadClient(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(requireContext())
            val user = db.userDao().getUserById(id)

            withContext(Dispatchers.Main) {
                if (user != null && isAdded) {
                    binding.tvClientName.text = "Клиент: ${user.name}"
                    binding.tvClientEmail.text = user.email
                    binding.etDiscount.setText(user.discount.toString())
                }
            }
        }
    }

    private fun saveClient() {
        val discount = binding.etDiscount.text.toString().toFloatOrNull() ?: 0f

        if (discount < 0 || discount > 100) {
            binding.etDiscount.error = "Скидка от 0 до 100%"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())

                if (clientId > 0) {
                    // ✅ Редактирование существующего — только скидка!
                    val user = db.userDao().getUserById(clientId)
                    if (user != null) {
                        db.userDao().updateUser(user.copy(discount = discount))
                        withContext(Dispatchers.Main) {
                            if (isAdded) Toast.makeText(requireContext(), "✅ Скидка обновлена", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // ✅ Новый клиент (через работника)
                    val name = binding.etName.text.toString().trim()
                    val email = binding.etEmail.text.toString().trim()
                    val phone = binding.etPhone.text.toString().trim()
                    val address = binding.etAddress.text.toString().trim()

                    if (name.isEmpty() || email.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            if (isAdded) Toast.makeText(requireContext(), "❌ Заполните имя и email", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    db.userDao().insertUser(
                        User(
                            0, email, email, "123456",
                            com.example.pract7_v8.db.UserRole.CLIENT,
                            name, phone, discount, address, null, false
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (isAdded) Toast.makeText(requireContext(), "✅ Клиент добавлен", Toast.LENGTH_SHORT).show()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (isAdded) findNavController().navigateUp()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}