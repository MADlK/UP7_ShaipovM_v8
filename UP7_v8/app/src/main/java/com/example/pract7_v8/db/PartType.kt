package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "part_types")
data class PartType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val description: String? = null
)