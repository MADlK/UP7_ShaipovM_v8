package com.example.pract7_v8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.databinding.FragmentOrderDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*



class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    // ✅ Читаем orderId из Bundle (без Safe Args)
    private val orderId: Int by lazy {
        arguments?.getInt("orderId", -1) ?: -1
    }

    private lateinit var adapter: OrderItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = OrderItemsAdapter()
        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItems.adapter = adapter

        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val order = withContext(Dispatchers.IO) {
                    db.orderDao().getOrderById(orderId)
                }

                if (order != null && isAdded) {
                    val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(Date(order.orderDate))
                    binding.tvOrderInfo.text = """
                        Заказ #${order.id}
                        📅 $date
                        📊 Статус: ${order.status.label}
                        💰 Сумма: ${order.totalAmount} руб.
                        ${order.deliveryAddress?.let { "📍 $it" } ?: ""}
                    """.trimIndent()

                    val items = withContext(Dispatchers.IO) {
                        db.orderDao().getOrderItems(order.id)
                    }
                    adapter.submitList(items)
                }
            } catch (e: Exception) {
                if (isAdded) {
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