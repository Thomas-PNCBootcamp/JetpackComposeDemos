package com.pnc.jetpackcomposedemos.testutil.builders

import com.pnc.jetpackcomposedemos.features.orders.domain.LineItem

fun aLineItem(
    id:Int = 1,
    productName:String = "Widget",
    orderQty:Int = 1,
    unitPrice:Double = 10.0,
    lineTotal:Double = orderQty * unitPrice, //derived from ealier parameters



) = LineItem(id,productName,orderQty,unitPrice,lineTotal)

    //when invoking the function, overriding default order qty and or unit price
//will result in a correct linetotal being calculated
// (Provided THAT default is not overridden)

