package com.example.pract7_v8



import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pract7_v8.db.*
import com.example.pract7_v8.databinding.ActivityMainBinding
import com.example.pract7_v8.SupplierViewModelFactory
import com.example.pract7_v8.FurnitureTypeViewModelFactory
import com.example.pract7_v8.AuthViewModelFactory
import com.example.pract7_v8.R
import com.example.pract7_v8.db.AppDatabase
import com.example.pract7_v8.db.UserRole
import com.example.pract7_v8.AuthRepository
import com.example.pract7_v8.AuthViewModel
import com.example.pract7_v8.LoginResult

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "FurniturePrefs"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_ROLE = "user_role"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // ✅ ПРОВЕРЯЕМ ЕСЛИ УЖЕ ВОШЁЛ
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            val role = UserRole.valueOf(sharedPreferences.getString(KEY_USER_ROLE, UserRole.WORKER.name)!!)
            val destination = when (role) {
                UserRole.WORKER -> R.id.workerDashboardFragment
                UserRole.CLIENT -> R.id.furnitureTypeListFragment
                UserRole.SUPPLIER -> R.id.supplierDashboardFragment
            }
            navController.navigate(destination)
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }

    fun clearLoginStatus() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        navController.navigate(R.id.loginFragment)
    }
}

