package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "furniture_types")
data class FurnitureType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val basePrice: Double = 0.0,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)