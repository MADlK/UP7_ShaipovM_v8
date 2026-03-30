package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "supplier_parts",
    foreignKeys = [
        ForeignKey(
            entity = Supplier::class,
            parentColumns = ["id"],
            childColumns = ["supplierId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PartCharacteristic::class,
            parentColumns = ["id"],
            childColumns = ["partCharacteristicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("supplierId"), Index("partCharacteristicId")]
)
data class SupplierPart(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val supplierId: Int,
    val partCharacteristicId: Int,
    val price: Double,
    val availableQuantity: Int,
    val leadTimeDays: Int = 7
)