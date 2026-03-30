package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.R
import com.example.pract7_v8.databinding.FragmentClientCatalogBinding
import com.example.pract7_v8.MainActivity

class ClientCatalogFragment : Fragment() {

    private var _binding: FragmentClientCatalogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Переход к каталогу мебели
        binding.btnViewCatalog.setOnClickListener {
            findNavController().navigate(R.id.furnitureTypeListFragment)
        }

        // Мои заказы (заглушка)
        binding.btnMyOrders.setOnClickListener {
            // TODO: Добавить OrdersFragment
        }

        // Профиль (заглушка)
        binding.btnProfile.setOnClickListener {
            // TODO: Добавить ProfileFragment
        }

        // Выход
        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}