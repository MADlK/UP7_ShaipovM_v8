package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = com.example.pract7_v8.db.User::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: Int,  // ID пользователя-клиента
    val orderDate: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.PENDING,
    val totalAmount: Double,
    val deliveryAddress: String? = null,
    val comment: String? = null
)