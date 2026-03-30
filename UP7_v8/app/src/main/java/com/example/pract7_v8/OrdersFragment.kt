package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pract7_v8.databinding.FragmentOrdersBinding
import com.example.pract7_v8.MainActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*




class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Фильтр по статусу
        setupStatusFilter()

        adapter = OrderAdapter(
            onViewDetails = { orderId ->
                val bundle = Bundle()
                bundle.putInt("orderId", orderId)
                findNavController().navigate(R.id.orderDetailsFragment, bundle)
            },
            onChangeStatus = { order -> changeOrderStatus(order) }
        )

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOrders.adapter = adapter

        loadOrders()

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun setupStatusFilter() {
        val statuses = OrderStatus.values().map { it.label }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter

        binding.spinnerStatus.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadOrdersByStatus(OrderStatus.values()[position])
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun loadOrders() {
        loadOrdersByStatus(null)
    }

    private fun loadOrdersByStatus(status: OrderStatus?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val ordersFlow = if (status == null) {
                    db.orderDao().getAllOrders()
                } else {
                    db.orderDao().getOrdersByStatus(status)
                }

                ordersFlow.collectLatest { orders ->
                    if (!isActive || !isAdded || _binding == null) return@collectLatest
                    adapter.submitList(orders)
                }
            } catch (e: Exception) {
                if (isActive && isAdded) {
                    Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun changeOrderStatus(order: com.example.pract7_v8.db.Order) {
        val statuses = OrderStatus.values()
        val statusLabels = statuses.map { it.label }.toTypedArray()
        val currentPos = statuses.indexOf(order.status)

        AlertDialog.Builder(requireContext())
            .setTitle("📋 Изменить статус заказа #${order.id}")
            .setSingleChoiceItems(statusLabels, currentPos) { dialog, which ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val db = AppDatabase.getDatabase(requireContext())
                        withContext(Dispatchers.IO) {
                            db.orderDao().updateOrderStatus(order.id, statuses[which])
                        }
                        if (isActive && isAdded) {
                            Toast.makeText(requireContext(), "✅ Статус обновлён", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        if (isActive && isAdded) {
                            Toast.makeText(requireContext(), "❌ ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}