package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.databinding.ItemMyPartBinding


import com.example.pract7_v8.db.SupplierPart


class MyPartsAdapter(
    private val onDeleteClick: (SupplierPart) -> Unit
) : ListAdapter<SupplierPart, MyPartsAdapter.MyPartViewHolder>(MyPartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPartViewHolder {
        val binding = ItemMyPartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyPartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyPartViewHolder(private val binding: ItemMyPartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(supplierPart: SupplierPart) {
            // Название детали (пока показываем ID, можно загрузить имя из БД)
            binding.tvPartName.text = "Деталь #${supplierPart.partCharacteristicId}"

            // Тип детали (заглушка — можно загрузить через JOIN)
            binding.tvPartType.text = "Тип детали"

            // Цена
            binding.tvPrice.text = "${supplierPart.price} руб."

            // Количество (твоё поле: availableQuantity)
            binding.tvQuantity.text = "${supplierPart.availableQuantity} шт."

            // Срок доставки (твоё поле: leadTimeDays)
            binding.tvDelivery.text = "${supplierPart.leadTimeDays} дн."

            // Кнопка удаления
            binding.btnDelete.setOnClickListener {
                onDeleteClick(supplierPart)
            }
        }
    }

    class MyPartDiffCallback : DiffUtil.ItemCallback<SupplierPart>() {
        override fun areItemsTheSame(oldItem: SupplierPart, newItem: SupplierPart) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SupplierPart, newItem: SupplierPart) =
            oldItem == newItem
    }
}