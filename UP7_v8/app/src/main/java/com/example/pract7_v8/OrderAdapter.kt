package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.db.Order
import com.example.pract7_v8.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.*
import android.os.Bundle


import android.view.View

import android.widget.TextView

import com.example.pract7_v8.db.OrderStatus


class OrderAdapter(
    // ✅ Передаём orderId (Int) а не Order
    private val onViewDetails: (Int) -> Unit,
    private val onChangeStatus: ((Order) -> Unit)? = null
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(order: Order) {
            val tvOrderId = view.findViewById<TextView>(R.id.tvOrderId)
            val tvOrderDate = view.findViewById<TextView>(R.id.tvOrderDate)
            val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
            val tvTotal = view.findViewById<TextView>(R.id.tvTotal)
            val tvAddress = view.findViewById<TextView>(R.id.tvAddress)
            val btnViewDetails = view.findViewById<TextView>(R.id.btnViewDetails)
            val btnChangeStatus = view.findViewById<View>(R.id.btnChangeStatus)

            // ID заказа
            tvOrderId.text = "Заказ #${order.id}"

            // Дата
            val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(Date(order.orderDate))
            tvOrderDate.text = "📅 $date"

            // Статус
            tvStatus.text = order.status.label
            tvStatus.setTextColor(
                when (order.status) {
                    OrderStatus.PENDING -> 0xFFFFA726.toInt()
                    OrderStatus.IN_PROGRESS -> 0xFF42A5F5.toInt()
                    OrderStatus.COMPLETED -> 0xFF66BB6A.toInt()
                    OrderStatus.CANCELLED -> 0xFFEF5350.toInt()
                }
            )

            // Сумма
            tvTotal.text = "${order.totalAmount} руб."

            // Адрес
            if (!order.deliveryAddress.isNullOrBlank()) {
                tvAddress.text = "📍 ${order.deliveryAddress}"
                tvAddress.visibility = View.VISIBLE
            } else {
                tvAddress.visibility = View.GONE
            }

            // ✅ Кнопка детали — передаём orderId (Int)
            btnViewDetails.setOnClickListener {
                onViewDetails(order.id)  // ✅ Int, не Order!
            }

            // Кнопка статуса (только для работника)
            onChangeStatus?.let { callback ->
                btnChangeStatus.visibility = View.VISIBLE
                btnChangeStatus.setOnClickListener { callback(order) }
            } ?: run {
                btnChangeStatus.visibility = View.GONE
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order) = oldItem == newItem
    }
}