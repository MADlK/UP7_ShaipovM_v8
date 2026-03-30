package com.example.pract7_v8.db

import androidx.room.*
import com.example.pract7_v8.db.User
import com.example.pract7_v8.db.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE email = :email OR login = :login LIMIT 1")
    suspend fun getUserByCredentials(email: String, login: String): User?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'CLIENT' ORDER BY name")
    suspend fun getClientsList(): List<User>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersList(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    // ✅ ЧЁРНЫЙ СПИСОК — добавить/убрать
    @Query("UPDATE users SET isBlacklisted = :blacklisted WHERE id = :id")
    suspend fun setBlacklisted(id: Int, blacklisted: Boolean)

    // ✅ Получить всех чёрных клиентов
    @Query("SELECT * FROM users WHERE role = 'CLIENT' AND isBlacklisted = 1 ORDER BY name")
    suspend fun getBlacklistedClients(): List<User>
}