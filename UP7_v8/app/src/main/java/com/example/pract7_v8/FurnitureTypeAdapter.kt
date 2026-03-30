package com.example.pract7_v8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pract7_v8.db.FurnitureType
import com.example.pract7_v8.databinding.FragmentFurnitureTypeListBinding
import com.squareup.picasso.Picasso
import com.example.pract7_v8.databinding.ItemFurnitureTypeBinding
import java.io.File










class FurnitureTypeAdapter(
    private val onEditClick: ((FurnitureType) -> Unit)? = null,
    private val onDeleteClick: ((FurnitureType) -> Unit)? = null,
    private val onAddToCart: ((FurnitureType) -> Unit)? = null
) : ListAdapter<FurnitureType, FurnitureTypeAdapter.FurnitureViewHolder>(FurnitureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FurnitureViewHolder {
        val binding = ItemFurnitureTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FurnitureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FurnitureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FurnitureViewHolder(private val binding: ItemFurnitureTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ft: FurnitureType) {
            binding.tvFurnitureName.text = ft.name
            binding.tvFurnitureDescription.text = ft.description ?: "Без описания"
            binding.tvFurniturePrice.text = "${ft.basePrice} руб."

            val imagePath = ft.imageUrl
            if (!imagePath.isNullOrBlank()) {
                val file = File(imagePath)
                if (file.exists()) {
                    Picasso.get().load(file).centerCrop().fit().into(binding.ivFurnitureImage)
                } else {
                    Picasso.get().load(imagePath).centerCrop().fit().into(binding.ivFurnitureImage)
                }
            } else {
                binding.ivFurnitureImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // ✅ Режим работника
            if (onEditClick != null && onDeleteClick != null) {
                binding.btnAddToCart.visibility = ViewGroup.GONE
                binding.btnEdit.visibility = ViewGroup.VISIBLE
                binding.btnDelete.visibility = ViewGroup.VISIBLE

                binding.btnEdit.setOnClickListener { onEditClick?.let { it1 -> it1(ft) } }
                binding.btnDelete.setOnClickListener { onDeleteClick?.let { it1 -> it1(ft) } }
            }
            // ✅ Режим клиента
            else if (onAddToCart != null) {
                binding.btnAddToCart.visibility = ViewGroup.VISIBLE
                binding.btnEdit.visibility = ViewGroup.GONE
                binding.btnDelete.visibility = ViewGroup.GONE

                binding.btnAddToCart.setOnClickListener { onAddToCart?.let { it1 -> it1(ft) } }
            }
        }
    }

    class FurnitureDiffCallback : DiffUtil.ItemCallback<FurnitureType>() {
        override fun areItemsTheSame(oldItem: FurnitureType, newItem: FurnitureType) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: FurnitureType, newItem: FurnitureType) =
            oldItem == newItem
    }
}