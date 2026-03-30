package com.example.pract7_v8

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pract7_v8.db.User
import com.example.pract7_v8.db.UserRole
import com.example.pract7_v8.AuthRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.Supplier

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.login(email, password)
                if (user != null) {
                    _loginResult.value = LoginResult.Success(user)
                } else {
                    _loginResult.value = LoginResult.Error("Неверный email или пароль")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun register(
        email: String, login: String, password: String, name: String,
        role: UserRole, phone: String? = null, discount: Float = 0f,
        address: String? = null
    ) {
        viewModelScope.launch {
            try {
                val user = User(
                    email = email,
                    login = login,
                    password = password,
                    role = role,
                    name = name,
                    phone = phone,
                    discount = discount,
                    address = address,
                    supplierId = null
                )

                val userId = repository.register(user)

                if (userId > 0) {
                    _loginResult.value = LoginResult.Success(user.copy(id = userId.toInt()))
                } else {
                    _loginResult.value = LoginResult.Error("Ошибка регистрации")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun logout() {
        _loginResult.value = null
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}