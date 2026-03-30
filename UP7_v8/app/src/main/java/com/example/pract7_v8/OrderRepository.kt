package com.example.pract7_v8

import com.example.pract7_v8.db.OrderStatus
import com.example.pract7_v8.db.Order
import com.example.pract7_v8.db.OrderItem
import com.example.pract7_v8.db.OrderDao
import kotlinx.coroutines.flow.Flow

class OrderRepository(private val dao: OrderDao) {

    // Orders
    fun getOrdersByClient(clientId: Int): Flow<List<Order>> = dao.getOrdersByClient(clientId)
    fun getAllOrders(): Flow<List<Order>> = dao.getAllOrders()
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> = dao.getOrdersByStatus(status)
    suspend fun getOrderById(id: Int): Order? = dao.getOrderById(id)
    suspend fun insertOrder(order: Order): Long = dao.insertOrder(order)
    suspend fun updateOrder(order: Order) = dao.updateOrder(order)
    suspend fun updateOrderStatus(orderId: Int, status: OrderStatus) = dao.updateOrderStatus(orderId, status)
    suspend fun deleteOrder(order: Order) = dao.deleteOrder(order)

    // OrderItems
    suspend fun getOrderItems(orderId: Int): List<OrderItem> = dao.getOrderItems(orderId)
    fun getOrderItemsFlow(orderId: Int): Flow<List<OrderItem>> = dao.getOrderItemsFlow(orderId)
    suspend fun insertOrderItem(item: OrderItem): Long = dao.insertOrderItem(item)
    suspend fun insertOrderItems(items: List<OrderItem>) = dao.insertOrderItems(items)
    suspend fun deleteOrderItems(orderId: Int) = dao.deleteOrderItems(orderId)
}