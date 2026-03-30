package com.example.pract7_v8

import com.example.pract7_v8.db.FurnitureType

data class CartItem(
    val furniture: FurnitureType,
    var quantity: Int = 1
) {
    fun getSubtotal(): Double = furniture.basePrice * quantity
}

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun addItem(furniture: FurnitureType) {
        val existing = cartItems.find { it.furniture.id == furniture.id }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(furniture))
        }
    }

    fun removeItem(furnitureId: Int) {
        cartItems.removeAll { it.furniture.id == furnitureId }
    }

    fun updateQuantity(furnitureId: Int, quantity: Int) {
        val item = cartItems.find { it.furniture.id == furnitureId }
        if (item != null) {
            if (quantity > 0) {
                item.quantity = quantity
            } else {
                removeItem(furnitureId)
            }
        }
    }

    fun clear() {
        cartItems.clear()
    }

    fun getItems(): List<CartItem> = cartItems.toList()

    fun getTotal(): Double = cartItems.sumOf { it.getSubtotal() }

    fun isEmpty(): Boolean = cartItems.isEmpty()

    fun getCount(): Int = cartItems.sumOf { it.quantity }
}