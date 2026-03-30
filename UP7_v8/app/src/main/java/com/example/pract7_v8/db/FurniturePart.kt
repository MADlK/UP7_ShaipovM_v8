package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "furniture_parts",
    foreignKeys = [
        ForeignKey(
            entity = FurnitureItem::class,
            parentColumns = ["id"],
            childColumns = ["furnitureItemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PartCharacteristic::class,
            parentColumns = ["id"],
            childColumns = ["partCharacteristicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("furnitureItemId"), Index("partCharacteristicId")]
)
data class FurniturePart(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val furnitureItemId: Int,
    val partCharacteristicId: Int,
    val quantity: Int
)