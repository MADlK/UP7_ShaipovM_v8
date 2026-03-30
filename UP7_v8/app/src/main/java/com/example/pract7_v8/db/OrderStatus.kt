package com.example.pract7_v8.db

enum class OrderStatus(val label: String)
{
    PENDING("Новый"),
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнен"),
    CANCELLED("Отменён")
}