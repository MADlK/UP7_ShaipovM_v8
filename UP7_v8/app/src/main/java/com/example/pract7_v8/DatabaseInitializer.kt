package com.example.pract7_v8

import android.content.Context
import com.example.pract7_v8.db.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log




object DatabaseInitializer {

    fun initialize(context: Context) {
        Log.d("DatabaseInitializer", "Starting initialization")

        val db = AppDatabase.getDatabase(context)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Проверяем, пуста ли база (по пользователям)
                val users = db.userDao().getAllUsersList()

                if (users.isEmpty()) {
                    Log.d("DatabaseInitializer", "Database is empty, populating...")
                    populateDatabase(db)
                    Log.d("DatabaseInitializer", "Database populated successfully")
                } else {
                    Log.d("DatabaseInitializer", "Database already has ${users.size} users")
                }
            } catch (e: Exception) {
                Log.e("DatabaseInitializer", "Error: ${e.message}", e)
            }
        }
    }

    private suspend fun populateDatabase(db: AppDatabase) {
        try {
            // ===== 1. Сначала создаём Поставщика =====
            val supplierId = db.supplierDao().insertSupplier(
                Supplier(
                    id = 0,
                    name = "ООО Крепёж",
                    email = "info@krepezh.ru",
                    phone = "+7 (495) 123-45-67",
                    address = "г. Москва, ул. Примерная, д. 1",
                    contactPerson = "Иванов Иван"
                )
            ).toInt()

            Log.d("DatabaseInitializer", "Supplier created with ID: $supplierId")

            // ===== 2. Создаём пользователя-поставщика с supplierId =====
            val supplierUserId = db.userDao().insertUser(
                User(
                    id = 0,
                    email = "supplier@krepezh.ru",
                    login = "supplier",
                    password = "123456",
                    role = UserRole.SUPPLIER,
                    name = "ООО Крепёж",
                    phone = "+7 (495) 123-45-67",
                    discount = 0f,
                    address = "г. Москва, ул. Примерная, д. 1",
                    supplierId = supplierId
                )
            )

            Log.d("DatabaseInitializer", "Supplier user created with ID: $supplierUserId, supplierId: $supplierId")

            // ===== 3. Создаём тестовые типы деталей =====
            val screwTypeId = db.partTypeDao().insertPartType(
                PartType(0, "Шурупы", "Крепёж", "Стальные шурупы")
            ).toInt()

            val boltTypeId = db.partTypeDao().insertPartType(
                PartType(0, "Болты", "Крепёж", "Стальные болты")
            ).toInt()

            Log.d("DatabaseInitializer", "Part types created")

            // ===== 4. Создаём характеристики деталей =====
            val partCharId1 = db.partCharacteristicDao().insertPartCharacteristic(
                PartCharacteristic(
                    id = 0,
                    partTypeId = screwTypeId,
                    length = 50f,
                    material = "Сталь",
                    diameter = 5f,
                    name = "Шуруп 50мм"
                )
            ).toInt()

            val partCharId2 = db.partCharacteristicDao().insertPartCharacteristic(
                PartCharacteristic(
                    id = 0,
                    partTypeId = boltTypeId,
                    length = 100f,
                    material = "Сталь",
                    diameter = 10f,
                    name = "Болт M10x100"
                )
            ).toInt()

            Log.d("DatabaseInitializer", "Part characteristics created")

            // ===== 5. Создаём детали поставщика =====
            db.supplierPartDao().insertSupplierPart(
                SupplierPart(
                    id = 0,
                    supplierId = supplierId,
                    partCharacteristicId = partCharId1,
                    price = 15.50,
                    availableQuantity = 1000,
                    leadTimeDays = 5
                )
            )



            Log.d("DatabaseInitializer", "Supplier parts created")

            // ===== 6. Создаём тестового работника =====
            db.userDao().insertUser(
                User(
                    id = 0,
                    email = "worker@furniture.ru",
                    login = "worker",
                    password = "123456",
                    role = UserRole.WORKER,
                    name = "Иванов Петр",
                    phone = "+7 (999) 111-22-33",
                    discount = 0f,
                    address = "г. Москва, ул. Рабочая, д. 1",
                    supplierId = null  // У работника нет поставщика
                )
            )

            // ===== 7. Создаём тестового клиента =====
            db.userDao().insertUser(
                User(
                    id = 0,
                    email = "client@mail.ru",
                    login = "client",
                    password = "123456",
                    role = UserRole.CLIENT,
                    name = "Сидорова Анна",
                    phone = "+7 (999) 444-55-66",
                    discount = 10f,
                    address = "г. Санкт-Петербург, пр. Невский, д. 10",
                    supplierId = null  // У клиента нет поставщика
                )
            )

            Log.d("DatabaseInitializer", "All test data created successfully")

        } catch (e: Exception) {
            Log.e("DatabaseInitializer", "Error populating database: ${e.message}", e)
        }
    }
}