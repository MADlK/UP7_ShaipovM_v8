package com.example.pract7_v8

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.R
import com.example.pract7_v8.db.*
import com.example.pract7_v8.db.UserRole
import com.example.pract7_v8.AuthRepository
import com.example.pract7_v8.databinding.FragmentLoginBinding
import com.example.pract7_v8.AuthViewModel
import com.example.pract7_v8.AuthViewModelFactory
import com.example.pract7_v8.LoginResult
import android.util.Log


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация ViewModel
        val database = AppDatabase.getDatabase(requireContext())
        val repository = AuthRepository(database.userDao())
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(repository)
        )[AuthViewModel::class.java]

        // Кнопка входа
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (viewModel.validateEmail(email) && viewModel.validatePassword(password)) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "❌ Проверь email и пароль", Toast.LENGTH_SHORT).show()
            }
        }

        // Кнопка регистрации
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Наблюдение за результатом входа
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Success -> {
                    // ✅ ПРОВЕРКА: клиент в чёрном списке?
                    if (result.user.role == UserRole.CLIENT && result.user.isBlacklisted) {
                        Toast.makeText(requireContext(), "❌ Ваш аккаунт заблокирован. Обратитесь к администратору.", Toast.LENGTH_LONG).show()
                        return@observe
                    }

                    Toast.makeText(requireContext(), "✅ Вход успешен!", Toast.LENGTH_SHORT).show()

                    val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
                    with(prefs.edit()) {
                        putBoolean("is_logged_in", true)
                        putInt("user_id", result.user.id)
                        putString("user_role", result.user.role.name)
                        putInt("supplier_id", result.user.supplierId ?: 0)
                        apply()
                    }

                    val destination = when (result.user.role) {
                        UserRole.WORKER -> R.id.workerDashboardFragment
                        UserRole.CLIENT -> R.id.furnitureTypeListFragment
                        UserRole.SUPPLIER -> R.id.supplierDashboardFragment
                    }
                    findNavController().navigate(destination)
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), "❌ ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}