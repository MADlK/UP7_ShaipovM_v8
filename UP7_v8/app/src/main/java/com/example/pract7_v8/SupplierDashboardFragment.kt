package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.R
import com.example.pract7_v8.databinding.FragmentSupplierDashboardBinding
import com.example.pract7_v8.MainActivity
import android.widget.Toast


class SupplierDashboardFragment : Fragment() {

    private var _binding: FragmentSupplierDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Мои детали
        binding.btnMyParts.setOnClickListener {


                findNavController().navigate(R.id.action_supplierDashboard_to_myPartsFragment)


        }

        // ✅ Заказы
        binding.btnOrders.setOnClickListener {
            findNavController().navigate(R.id.action_supplierDashboard_to_ordersFragment)
        }

        // ✅ Профиль
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_supplierDashboard_to_profileFragment)
        }

        // ✅ Выход
        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}