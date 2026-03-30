package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "part_characteristics",
    foreignKeys = [
        ForeignKey(
            entity = PartType::class,
            parentColumns = ["id"],
            childColumns = ["partTypeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("partTypeId")]
)
data class PartCharacteristic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val partTypeId: Int,
    val length: Float,
    val material: String,
    val diameter: Float,
    val name: String
    // ❌ НЕТ поля weight!
)