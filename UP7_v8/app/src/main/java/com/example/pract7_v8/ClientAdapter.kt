package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.db.User
import com.example.pract7_v8.databinding.ItemClientBinding

class ClientAdapter(
    private val onEditClick: (User) -> Unit,
    private val onBlacklistClick: (User) -> Unit
) : ListAdapter<User, ClientAdapter.ClientViewHolder>(ClientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClientViewHolder(private val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(client: User) {
            binding.tvClientName.text = client.name
            binding.tvClientEmail.text = client.email
            binding.tvClientPhone.text = client.phone ?: "Нет телефона"
            binding.tvClientDiscount.text = "Скидка: ${client.discount}%"

            // ✅ Метка если в чёрном списке
            if (client.isBlacklisted) {
                binding.tvClientName.text = "🚫 ${client.name} (заблокирован)"
                binding.tvClientName.setTextColor(android.graphics.Color.RED)
            } else {
                binding.tvClientName.setTextColor(android.graphics.Color.parseColor("#212121"))
            }

            binding.btnEdit.setOnClickListener { onEditClick(client) }

            // ✅ Кнопка меняет текст в зависимости от статуса
            binding.btnBlacklist.text = if (client.isBlacklisted) "✅ Разблокировать" else "🚫 В чёрный список"
            binding.btnBlacklist.setOnClickListener { onBlacklistClick(client) }
        }
    }

    class ClientDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}