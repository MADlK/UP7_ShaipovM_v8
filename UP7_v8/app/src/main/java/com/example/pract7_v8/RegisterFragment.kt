package com.example.pract7_v8

import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.R
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.UserRole
import com.example.pract7_v8.AuthRepository
import com.example.pract7_v8.databinding.FragmentRegisterBinding
import com.example.pract7_v8.AuthViewModel
import com.example.pract7_v8.AuthViewModelFactory
import android.util.Log
import com.example.pract7_v8.LoginResult
import com.example.pract7_v8.db.Supplier




import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AuthRepository(database.userDao())
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(repository)
        )[AuthViewModel::class.java]

        setupRoleSpinner()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (validateInput(email, login, password, confirmPassword, name)) {
                val selectedRole = when (binding.spinnerRole.selectedItemPosition) {
                    0 -> UserRole.WORKER
                    1 -> UserRole.CLIENT
                    2 -> UserRole.SUPPLIER
                    else -> UserRole.WORKER
                }

                val discount = if (selectedRole == UserRole.CLIENT) {
                    binding.etDiscount.text.toString().toFloatOrNull() ?: 0f
                } else 0f

                // Регистрируем пользователя
                viewModel.register(email, login, password, name, selectedRole, phone, discount, address)

                // ✅ Если поставщик — создаём Supplier после регистрации
                if (selectedRole == UserRole.SUPPLIER) {
                    viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                        if (result is LoginResult.Success) {
                            // Создаём Supplier в отдельной корутине
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val db = AppDatabase.getDatabase(requireContext())
                                    val supplierId = db.supplierDao().insertSupplier(
                                        Supplier(
                                            id = 0,
                                            name = name,
                                            email = email,
                                            phone = phone,
                                            address = address,
                                            contactPerson = login
                                        )
                                    ).toInt()

                                    // Обновляем пользователя с supplierId
                                    if (supplierId > 0) {
                                        val updatedUser = result.user.copy(supplierId = supplierId)
                                        db.userDao().updateUser(updatedUser)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    Toast.makeText(requireContext(), "✅ Регистрация успешна! Войдите.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), "❌ ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Работник", "Клиент", "Поставщик")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.spinnerRole.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.tilDiscount.visibility = if (position == 1) View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                binding.tilDiscount.visibility = View.GONE
            }
        }
    }

    private fun validateInput(email: String, login: String, password: String, confirmPassword: String, name: String): Boolean {
        if (!viewModel.validateEmail(email)) {
            binding.etEmail.error = "Неверный формат email"
            return false
        }
        if (login.length < 3) {
            binding.etLogin.error = "Логин минимум 3 символа"
            return false
        }
        if (!viewModel.validatePassword(password)) {
            binding.etPassword.error = "Пароль минимум 6 символов"
            return false
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Пароли не совпадают"
            return false
        }
        if (name.isEmpty()) {
            binding.etName.error = "Введите имя"
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}