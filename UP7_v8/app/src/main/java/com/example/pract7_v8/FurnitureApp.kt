package com.example.pract7_v8



import android.app.Application
import android.util.Log
import com.example.pract7_v8.DatabaseInitializer

class FurnitureApp : Application() {
    override fun onCreate() {
        super.onCreate()
        android.util.Log.e("DEBUG_App", "FurnitureApp.onCreate() called")
        DatabaseInitializer.initialize(this)
    }
}