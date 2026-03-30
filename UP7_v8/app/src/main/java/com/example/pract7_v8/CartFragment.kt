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
import com.example.pract7_v8.CartManager
import com.example.pract7_v8.db.OrderStatus
import com.example.pract7_v8.db.Order
import com.example.pract7_v8.db.OrderItem
import com.example.pract7_v8.databinding.FragmentCartBinding
import com.example.pract7_v8.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (_binding == null) return

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = CartAdapter(
            onUpdateQuantity = { furnitureId, quantity ->
                CartManager.updateQuantity(furnitureId, quantity)
                updateCart()  // ✅ Пересчитываем всё после изменения
            },
            onRemoveItem = { furnitureId ->
                CartManager.removeItem(furnitureId)
                updateCart()
            }
        )

        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter

        updateCart()

        binding.btnCheckout.setOnClickListener {
            checkout()
        }

        // ✅ Кнопка "Мои заказы" после оформления
        binding.btnMyOrders.setOnClickListener {
            findNavController().navigate(R.id.myOrdersFragment)
        }

        binding.btnLogout.setOnClickListener {
            (requireActivity() as? MainActivity)?.clearLoginStatus()
        }
    }

    private fun updateCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
            val clientId = prefs.getInt("user_id", -1)

            val db = AppDatabase.getDatabase(requireContext())
            val client = withContext(Dispatchers.IO) {
                db.userDao().getUserById(clientId)
            }

            val items = CartManager.getItems()
            adapter.submitList(items)

            // Считаем сумму
            val subtotal = CartManager.getTotal()

            // ✅ Применяем скидку
            val discountPercent = client?.discount ?: 0f
            val discountAmount = subtotal * (discountPercent / 100)
            val total = subtotal - discountAmount

            // ✅ Округляем до 2 знаков
            binding.tvTotal.text = "Итого: ${String.format("%.2f", total)} руб."
            binding.tvItemsCount.text = "Товаров: ${CartManager.getCount()}"

            // ✅ Показываем скидку если она есть
            if (discountPercent > 0) {
                binding.tvDiscount.text = "🏷️ Скидка: ${discountPercent}% (-${String.format("%.2f", discountAmount)} руб.)"
                binding.tvDiscount.visibility = View.VISIBLE
            } else {
                binding.tvDiscount.visibility = View.GONE
            }

            binding.btnCheckout.isEnabled = !CartManager.isEmpty()
            binding.tvEmptyState.visibility = if (CartManager.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewCart.visibility = if (CartManager.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun checkout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prefs = requireContext().getSharedPreferences("FurniturePrefs", Context.MODE_PRIVATE)
                val clientId = prefs.getInt("user_id", -1)

                if (clientId == -1) {
                    Toast.makeText(requireContext(), "❌ Войдите в аккаунт", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val cartItems = CartManager.getItems()
                if (cartItems.isEmpty()) {
                    Toast.makeText(requireContext(), "❌ Корзина пуста", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val db = AppDatabase.getDatabase(requireContext())

                // ✅ 1. Получаем клиента из БД (чтобы взять скидку)
                val client = withContext(Dispatchers.IO) {
                    db.userDao().getUserById(clientId)
                }

                if (client == null) {
                    Toast.makeText(requireContext(), "❌ Клиент не найден", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // ✅ 2. Получаем скидку клиента (от 0 до 100)
                val discountPercent = client.discount  // например, 15.0 = 15%

                // ✅ 3. Считаем сумму ДО скидки
                val subtotal = cartItems.sumOf { it.getSubtotal() }

// ✅ 4. Считаем размер скидки в рублях
                val discountAmount = subtotal * (discountPercent / 100)

// ✅ 5. Итоговая сумма ПОСЛЕ скидки (округляем до 2 знаков)
                val total = Math.round((subtotal - discountAmount) * 100.0) / 100.0

// Создаём заказ
                val orderId = withContext(Dispatchers.IO) {
                    db.orderDao().insertOrder(
                        Order(
                            0,
                            clientId,
                            System.currentTimeMillis(),
                            OrderStatus.PENDING,
                            total,  // ✅ Округлённая сумма
                            prefs.getString("user_address", null),
                            "Скидка: ${discountPercent}% (-${String.format("%.2f", discountAmount)} руб.)"
                        )
                    )
                }

                // ✅ 6. Создаём позиции заказа (цены без скидки, скидка применяется к общему заказу)
                val orderItems = cartItems.map { cartItem ->
                    OrderItem(
                        0,
                        orderId.toInt(),
                        cartItem.furniture.id,
                        cartItem.furniture.name,
                        cartItem.quantity,
                        cartItem.furniture.basePrice,  // Цена без скидки
                        cartItem.getSubtotal()  // Подытог без скидки
                    )
                }

                withContext(Dispatchers.IO) {
                    db.orderDao().insertOrderItems(orderItems)
                }

                // Очищаем корзину
                CartManager.clear()

                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        // ✅ Показываем информацию о скидке
                        val message = if (discountPercent > 0) {
                            "✅ Заказ оформлен!\nСкидка: ${discountPercent}% (-${discountAmount} руб.)"
                        } else {
                            "✅ Заказ оформлен!"
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

                        // Показываем кнопку "Мои заказы"
                        binding.btnMyOrders.visibility = View.VISIBLE
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "❌ Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}