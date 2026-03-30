package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.R
import com.example.pract7_v8.databinding.FragmentWorkerDashboardBinding
import android.widget.Toast
import com.example.pract7_v8.MainActivity

class WorkerDashboardFragment : Fragment() {

    private var _binding: FragmentWorkerDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Проверка что binding инициализирован
        if (_binding == null) return

        // Карточка: Поставщики
        binding.btnSuppliers.setOnClickListener {
            safeNavigate(R.id.action_workerDashboard_to_supplierList)
        }

        // Карточка: Типы мебели
        binding.btnFurnitureTypes.setOnClickListener {
            safeNavigate(R.id.action_workerDashboard_to_furnitureTypeList)
        }

        // Карточка: Клиенты
        binding.btnClients.setOnClickListener {
            safeNavigate(R.id.action_workerDashboard_to_clientList)
        }

        // Карточка: Заказы
        binding.btnOrders.setOnClickListener {
            safeNavigate(R.id.action_workerDashboard_to_ordersFragment)
        }

        // Кнопка: Выход
        binding.btnLogout.setOnClickListener {
            try {
                (requireActivity() as? MainActivity)?.clearLoginStatus()
            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "❌ Ошибка выхода", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ✅ Безопасная навигация с проверками
    private fun safeNavigate(actionId: Int) {
        if (!isAdded || _binding == null) return
        try {
            findNavController().navigate(actionId)
        } catch (e: Exception) {
            if (isAdded) {
                Toast.makeText(requireContext(), "❌ Ошибка перехода", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // ✅ Обязательно!
    }
}