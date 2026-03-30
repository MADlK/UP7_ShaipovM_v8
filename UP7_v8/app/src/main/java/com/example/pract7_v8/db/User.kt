package com.example.pract7_v8.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    WORKER, CLIENT, SUPPLIER
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val login: String,
    val password: String,
    val role: UserRole,
    val name: String,
    val phone: String? = null,
    val discount: Float = 0f,
    val address: String? = null,
    val supplierId: Int? = null,
    val isBlacklisted: Boolean = false
)