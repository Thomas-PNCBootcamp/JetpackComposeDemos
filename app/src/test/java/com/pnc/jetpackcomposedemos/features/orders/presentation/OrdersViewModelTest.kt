package com.pnc.jetpackcomposedemos.features.orders.presentation

//import app.cash.turbine.test
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.pnc.jetpackcomposedemos.core.LoadableState
import com.pnc.jetpackcomposedemos.features.orders.data.FakeOrderRepository
import com.pnc.jetpackcomposedemos.features.orders.domain.GetOrdersUseCase
import com.pnc.jetpackcomposedemos.features.orders.domain.Order
import com.pnc.jetpackcomposedemos.features.orders.domain.OrderRepository
import com.pnc.jetpackcomposedemos.testutil.MainDispatcherRule
import com.pnc.jetpackcomposedemos.testutil.builders.anOrder
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class OrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModelWith(repo: OrderRepository): OrdersViewModel{
        return OrdersViewModel(GetOrdersUseCase(repo))
    }


    @Test
    fun `starts in loading state before anything is requested`() = runTest {
        val viewModel = viewModelWith(FakeOrderRepository())
        viewModel.uiState.test{
            assertEquals(LoadableState.Loading, awaitItem())

        }
    }

    @Test
    fun `given orders are available, when loading then moves from loading to success`() = runTest{
        val orders = listOf(anOrder { id = 1 })
        val viewModel = viewModelWith(FakeOrderRepository(orders))
        viewModel.uiState.test{
            assertEquals(LoadableState.Loading, awaitItem())
            viewModel.loadData()
            assertEquals(LoadableState.Success(orders), awaitItem())
        }

    }


    @Test
    fun `given the repo fails, when loading, them move from loading to error with its message`() = runTest{

        val repo = FakeOrderRepository(failure = IOException("network down"))
        val viewModel = viewModelWith(repo)


        viewModel.uiState.test{
            assertEquals(LoadableState.Loading, awaitItem())
            viewModel.loadData()
            assertEquals(LoadableState.Error("network down"), awaitItem())
        }



    }


    @Test
    fun`given an earlier failure, when retrying then shows loading again before sucess`() = runTest{
        val orders = listOf(anOrder{id = 1})
        var shouldFail = true
        val repo = object: OrderRepository{
            override suspend fun getOrders(): List<Order> {
                if(shouldFail) throw IOException("network down")
                return orders


            }
        }
        val viewModel = viewModelWith(repo)

        viewModel.uiState.test{
            assertEquals(LoadableState.Loading, awaitItem())

            viewModel.loadData()
            assertEquals(LoadableState.Error("network down"), awaitItem())

            shouldFail = false
            viewModel.loadData()
            assertEquals(LoadableState.Loading,awaitItem())
            assertEquals(LoadableState.Success(orders), awaitItem())
        }



    }

    @Test
    fun `given the load is cancelled mid flight, when the scope is cancelled, then does not report an error`() = runTest{

        val repo = object: OrderRepository{
            override suspend fun getOrders(): List<Order> {
                delay(5000)
                return emptyList()
            }

        }

        val viewModel = viewModelWith(repo)

        viewModel.loadData()
        advanceTimeBy(1000)
        viewModel.viewModelScope.cancel()
        advanceUntilIdle()
        assertEquals(LoadableState.Loading, viewModel.uiState.value)
    }




}