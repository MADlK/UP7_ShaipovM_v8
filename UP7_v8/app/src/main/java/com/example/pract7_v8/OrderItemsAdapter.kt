package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.db.OrderItem
import com.example.pract7_v8.databinding.ItemOrderItemBinding

class OrderItemsAdapter : ListAdapter<OrderItem, OrderItemsAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(private val binding: ItemOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {
            binding.tvFurnitureName.text = item.furnitureName
            binding.tvPrice.text = "${item.priceAtOrder} руб./шт."
            binding.tvQuantity.text = "Количество: ${item.quantity} шт."
            binding.tvSubtotal.text = "${item.subtotal} руб."
        }
    }

    class OrderItemDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem) = oldItem == newItem
    }
}