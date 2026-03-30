package com.example.pract7_v8

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.pract7_v8.databinding.FragmentMyOrdersBinding
import kotlinx.coroutines.isActive


class MyOrdersFragment : Fragment() {

    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ ПРАВИЛЬНО: FragmentMyOrdersBinding (от имени XML файла!)
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = OrderAdapter(
            onViewDetails = { orderId ->
                val bundle = Bundle()
                bundle.putInt("orderId", orderId)
                findNavController().navigate(R.id.orderDetailsFragment, bundle)
            }
        )

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOrders.adapter = adapter

        loadOrders()

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun loadOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
                val clientId = prefs.getInt("user_id", -1)

                if (clientId == -1) return@launch

                val db = AppDatabase.getDatabase(requireContext())
                db.orderDao().getOrdersByClient(clientId).collectLatest { orders ->
                    if (!isActive || !isAdded || _binding == null) return@collectLatest
                    adapter.submitList(orders)

                    binding.tvEmptyState.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerViewOrders.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE
                }
            } catch (e: Exception) {
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}