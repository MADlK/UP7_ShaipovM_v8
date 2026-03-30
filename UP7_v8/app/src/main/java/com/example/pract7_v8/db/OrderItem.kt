package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FurnitureType::class,
            parentColumns = ["id"],
            childColumns = ["furnitureTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId"), Index("furnitureTypeId")]
)
data class OrderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val furnitureTypeId: Int,
    val furnitureName: String,  // Копируем название на момент заказа
    val quantity: Int,
    val priceAtOrder: Double,  // Цена на момент заказа
    val subtotal: Double = quantity * priceAtOrder
)