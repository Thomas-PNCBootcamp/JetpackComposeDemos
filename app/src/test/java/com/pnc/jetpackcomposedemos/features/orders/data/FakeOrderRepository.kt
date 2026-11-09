package com.pnc.jetpackcomposedemos.features.orders.data

import com.pnc.jetpackcomposedemos.features.orders.domain.Order
import com.pnc.jetpackcomposedemos.features.orders.domain.OrderRepository

class FakeOrderRepository(
    private val orders: List<Order> = emptyList(),
    private val failure:Throwable? = null
): OrderRepository {

    var getOrdersCalls = 0
        private set

    override suspend fun getOrders(): List<Order>{
        getOrdersCalls++
        failure?.let{ throw it }

        return orders

    }


}