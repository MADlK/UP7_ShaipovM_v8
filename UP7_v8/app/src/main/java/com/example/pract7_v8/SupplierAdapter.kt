package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.db.Supplier
import com.example.pract7_v8.databinding.FragmentSupplierListBinding
import com.example.pract7_v8.databinding.ItemSupplierBinding

class SupplierAdapter(
    private val onDeleteClick: (Supplier) -> Unit
) : ListAdapter<Supplier, SupplierAdapter.SupplierViewHolder>(SupplierDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        val binding = ItemSupplierBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SupplierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SupplierViewHolder(private val binding: ItemSupplierBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(supplier: Supplier) {
            binding.tvSupplierName.text = supplier.name
            binding.tvSupplierEmail.text = supplier.email
            binding.tvSupplierPhone.text = supplier.phone ?: "📞 Не указан"
            binding.tvSupplierAddress.text = supplier.address ?: "📍 Адрес не указан"
            binding.tvContactPerson.text = "👤 ${supplier.contactPerson ?: "Не указан"}"

            binding.btnDelete.setOnClickListener {
                onDeleteClick(supplier)
            }
        }
    }

    class SupplierDiffCallback : DiffUtil.ItemCallback<Supplier>() {
        override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier) = oldItem == newItem
    }
}