package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.CartItem
import com.example.pract7_v8.databinding.ItemCartBinding
import com.squareup.picasso.Picasso
import java.io.File

class CartAdapter(
    private val onUpdateQuantity: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.tvFurnitureName.text = cartItem.furniture.name
            binding.tvPrice.text = "${cartItem.furniture.basePrice} руб./шт."


            updateSubtotal(cartItem)


            binding.etQuantity.setText(cartItem.quantity.toString())


            val imagePath = cartItem.furniture.imageUrl
            if (!imagePath.isNullOrBlank()) {
                val file = File(imagePath)
                if (file.exists()) {
                    Picasso.get().load(file).centerCrop().fit().into(binding.ivFurnitureImage)
                }
            }


            binding.etQuantity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val qty = binding.etQuantity.text.toString().toIntOrNull() ?: 1
                    if (qty > 0 && qty != cartItem.quantity) {
                        onUpdateQuantity(cartItem.furniture.id, qty)
                        updateSubtotal(cartItem.copy(quantity = qty))
                    }
                }
            }


            binding.etQuantity.setOnEditorActionListener { _, _, _ ->
                val qty = binding.etQuantity.text.toString().toIntOrNull() ?: 1
                if (qty > 0 && qty != cartItem.quantity) {
                    onUpdateQuantity(cartItem.furniture.id, qty)
                    updateSubtotal(cartItem.copy(quantity = qty))
                }
                true
            }


            binding.btnDelete.setOnClickListener {
                onRemoveItem(cartItem.furniture.id)
            }
        }


        private fun updateSubtotal(item: CartItem) {
            binding.tvSubtotal.text = "Итого: ${item.getSubtotal()} руб."
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.furniture.id == newItem.furniture.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem == newItem
    }
}