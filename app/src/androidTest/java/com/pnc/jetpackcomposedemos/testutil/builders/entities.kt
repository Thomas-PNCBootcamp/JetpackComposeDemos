package com.pnc.jetpackcomposedemos.testutil.builders

import com.pnc.jetpackcomposedemos.features.artists.data.local.ArtistEntity
import com.pnc.jetpackcomposedemos.features.orders.data.local.LineItemEntity
import com.pnc.jetpackcomposedemos.features.orders.data.local.OrderEntity
import com.pnc.jetpackcomposedemos.features.orders.data.local.OrderWithLineItemsEntity
import com.pnc.jetpackcomposedemos.features.todo.data.ToDoEntity
import java.time.LocalDateTime

fun anArtistEntity(
    id:Int = 1,
    name:String="Test artist",
    genre:String = "Rock",
    location:String = "New York, NY",
    imageUrl:String="",
    description:String = "a test artist",
    isAvailable:Boolean = true
)= ArtistEntity(id, name, genre, location, imageUrl, description, isAvailable)

fun anOrderEntity(
    id:Int = 1,
    customerId:Int = 100,
    firstName:String = "test",
    lastName:String = "test",
    orderDate:LocalDateTime = LocalDateTime.of(2024,1,1,0,0),
    shipDate:LocalDateTime = LocalDateTime.of(2024,1,3,0,0)

)= OrderEntity(id,customerId,firstName,lastName,orderDate,shipDate)


fun aLineEntity(
    id:Int = 1,
    orderId:Int = 1,
    productName:String = "test",
    orderQuantity:Int = 1,
    unitPrice:Double = 10.0,
    lineTotal:Double = orderQuantity * unitPrice
)= LineItemEntity(id,orderId,productName,orderQuantity,unitPrice,lineTotal)

fun anOrderWithItems(
    order:OrderEntity = anOrderEntity(),
    vararg items:LineItemEntity
) = OrderWithLineItemsEntity(order, items.toList())


fun aToDoEntity(
    id:Int = 1,
    title:String = "test",
    completed:Boolean = false
)= ToDoEntity(id,title,completed)




