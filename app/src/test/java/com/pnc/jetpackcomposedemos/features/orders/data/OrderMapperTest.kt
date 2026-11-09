package com.pnc.jetpackcomposedemos.features.orders.data

import com.pnc.jetpackcomposedemos.testutil.builders.aLineItemDto
import junit.framework.TestCase.assertEquals
import org.junit.Test

class OrderMapperTest {


    @Test
    fun `given two lines with same order number when mapping, then produces one order with both line items`(){

        //create a collection of orderlineItemDto
        //we only need to specify the properties that are relevant to this test
        val dtos = listOf(
            aLineItemDto(id = 1, orderNumber = 5, productName = "lamp",),
            aLineItemDto(id = 2, orderNumber = 5, productName = "shade",)

        )
        val orders = OrderMapper.mapToDomain(dtos)

        assertEquals(1, orders.size)
        assertEquals(listOf("lamp" , "shade"), orders.first().items.map{it.productName})
    }


    @Test
    fun `given two lines with different order number when mapping, then produces 2 orders `(){

        //create a collection of orderlineItemDto
        //we only need to specify the properties that are relevant to this test
        val dtos = listOf(
            aLineItemDto(id = 1, orderNumber = 5, productName = "lamp",),
            aLineItemDto(id = 2, orderNumber = 4, productName = "glasses",),
            aLineItemDto(id = 3, orderNumber = 4, productName = "chair",),
            aLineItemDto(id = 4, orderNumber = 4, productName = "shade",)

        )
        val orders = OrderMapper.mapToDomain(dtos)

        assertEquals(2, orders.size)
    }

}