package com.pnc.jetpackcomposedemos.testutil.builders

import com.pnc.jetpackcomposedemos.features.orders.data.MyOrderDto
import com.pnc.jetpackcomposedemos.features.orders.data.MyOrderLineDto
import com.pnc.jetpackcomposedemos.features.orders.data.MyOrderResponseDto

class myOrdersDtos {

    fun aMyOrdersResponse(
        customerId: Int = 1,
        vararg orders: MyOrderDto
    ) = MyOrderResponseDto(
        customerId = customerId,
        page = 1,
        pageSize = 100,
        totalOrders = orders.size,
        orders = orders.toList()
    )

}