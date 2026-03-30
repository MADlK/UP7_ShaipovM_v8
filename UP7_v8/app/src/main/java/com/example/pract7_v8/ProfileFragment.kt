package com.example.pract7_v8

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.databinding.FragmentProfileBinding
import com.example.pract7_v8.MainActivity


import android.widget.Toast

import androidx.lifecycle.lifecycleScope

import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.UserRole

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        loadUserProfile()

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun loadUserProfile() {
        binding.tvCompanyName.text = "Загрузка..."
        binding.tvEmail.text = ""
        binding.tvPhone.text = ""
        binding.tvAddress.text = ""
        binding.tvContactPerson.text = ""
        binding.tvStatus.text = ""

        lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
                val userId = prefs.getInt("user_id", -1)

                if (userId == -1) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "❌ Пользователь не найден", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    return@launch
                }

                val db = AppDatabase.getDatabase(requireContext())
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserById(userId)
                }

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        binding.tvCompanyName.text = user.name
                        binding.tvEmail.text = "📧 ${user.email}"
                        binding.tvPhone.text = user.phone?.let { "📞 $it" } ?: "📞 Не указан"
                        binding.tvAddress.text = user.address?.let { "📍 $it" } ?: "📍 Адрес не указан"
                        binding.tvContactPerson.text = "👤 ${user.login}"

                        val statusText = when (user.role) {
                            UserRole.WORKER -> "🔧 Роль: Работник"
                            UserRole.CLIENT -> "🛍️ Роль: Клиент"
                            UserRole.SUPPLIER -> "📦 Роль: Поставщик"
                        }
                        binding.tvStatus.text = statusText
                    } else {
                        Toast.makeText(requireContext(), "❌ Пользователь не найден в БД", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}