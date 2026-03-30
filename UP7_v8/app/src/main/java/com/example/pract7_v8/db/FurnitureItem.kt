package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "furniture_items",
    foreignKeys = [
        ForeignKey(
            entity = FurnitureType::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("typeId")]
)
data class FurnitureItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val typeId: Int,
    val name: String,
    val model: String,
    val description: String? = null,
    val price: Double,
    val imageUrl: String? = null,
    val inStock: Int = 0
)