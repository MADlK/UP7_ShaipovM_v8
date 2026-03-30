package com.example.pract7_v8.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters  // ✅ ИМПОРТ!
import com.example.pract7_v8.Converters
import com.example.pract7_v8.db.*


@Database(
    entities = [
        User::class,
        FurnitureType::class,
        FurnitureItem::class,
        PartType::class,
        PartCharacteristic::class,
        Supplier::class,
        SupplierPart::class,
        FurniturePart::class,
        Order::class,
        OrderItem::class
    ],
    version = 2,  // ✅ УВЕЛИЧЬТЕ С 1 ДО 2!
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun furnitureTypeDao(): FurnitureTypeDao
    abstract fun furnitureItemDao(): FurnitureItemDao
    abstract fun partTypeDao(): PartTypeDao
    abstract fun partCharacteristicDao(): PartCharacteristicDao
    abstract fun supplierDao(): SupplierDao
    abstract fun supplierPartDao(): SupplierPartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "furniture_production_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}