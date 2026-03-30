package com.example.pract7_v8

import com.example.pract7_v8.db.UserDao
import com.example.pract7_v8.db.User
import com.example.pract7_v8.db.UserRole
import android.util.Log
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.Supplier


class AuthRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, email)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    // ✅ ДОБАВЬ ЭТО:
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}