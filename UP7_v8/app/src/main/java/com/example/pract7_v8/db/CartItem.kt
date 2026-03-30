package com.example.pract7_v8.db

import com.example.pract7_v8.db.FurnitureType

data class CartItem(
    val furniture: FurnitureType,
    var quantity: Int = 1
) {
    fun getSubtotal(): Double = furniture.basePrice * quantity
}