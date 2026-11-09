package com.pnc.jetpackcomposedemos.features.orders.data

import com.pnc.jetpackcomposedemos.features.auth.data.storage.FakeTokenStore
import com.pnc.jetpackcomposedemos.features.auth.domain.SessionManager
import com.pnc.jetpackcomposedemos.features.auth.domain.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MyOrdersRepositoryTest {

    private class RecordingDataSource: MyOrdersDataSource{
        var requestedCustomerId: Int? = null
        override suspend fun getOrders(
            customerId: Int,
            page: Int,
            pageSize: Int
        ): MyOrderResponseDto {
            requestedCustomerId = customerId
            return MyOrderResponseDto(
                customerId = customerId,
                page = page,
                pageSize = pageSize,
                totalOrders = 0,
                orders = emptyList()
            )

        }
    }



    private val dataSource = RecordingDataSource()
    private val session = SessionManager(
        FakeTokenStore(),
        CoroutineScope(Dispatchers.Unconfined))
    private val repository = MyOrdersRepository(dataSource, session)



    @Test
    fun `asks for the signed in customers orders`() = runTest{
        session.signIn(User(id = "11000", username = "Mai", token = "t"))//sign in as Mai
        repository.getOrders()//load Mai's orders
        assertEquals(11000, dataSource.requestedCustomerId) //we should have asked for Mai's orders


    }


    @Test
    fun `refuses to load orders when nobody is signed in`() = runTest{
        var result = runCatching{repository.getOrders()} //runCatching returns a Result object
        assertTrue(result.exceptionOrNull() is IllegalStateException) //we expect that the function call will return the exception
        assertEquals(null,dataSource.requestedCustomerId) //never should have gotten this far

    }



}