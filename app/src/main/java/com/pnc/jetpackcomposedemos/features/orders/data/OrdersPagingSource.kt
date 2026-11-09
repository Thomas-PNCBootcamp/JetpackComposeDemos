package com.pnc.jetpackcomposedemos.features.orders.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pnc.jetpackcomposedemos.features.auth.domain.SessionManager
import com.pnc.jetpackcomposedemos.features.orders.domain.Order
import kotlin.coroutines.cancellation.CancellationException

//loads the signed in customers orders one server page at a time
//the key is the page number starting at 1
//every page uses the same page size b/c the server numbers its pages with it
class OrdersPagingSource(
    private val dataSource:MyOrdersDataSource,
    private val session: SessionManager,
    private val pageSize:Int
):PagingSource<Int, Order>(){

    companion object{
        const val FIRST_PAGE = 1
    }

    override suspend fun load(params:LoadParams<Int>): LoadResult<Int,Order>{

        val user = session.currentUser
            ?: return LoadResult.Error(IllegalStateException("you must be logged in to see orders"))

        val customerId = user.id.toIntOrNull()
            ?: return LoadResult.Error(IllegalStateException("The signed in user has no valid customer id"))

        val page = params.key ?: FIRST_PAGE

        return try{
            val response = dataSource.getOrders(customerId,page,pageSize)
            LoadResult.Page(
                data = MyOrderMapper.mapToDomain(response, user.username),
                prevKey = if(page == FIRST_PAGE) null else page - 1,
                nextKey = if(page * pageSize < response.totalOrders) page + 1 else null
            )

        }catch(e: CancellationException){
            throw e
        }catch(e: Exception){
            LoadResult.Error(e)
        }

    }

//what page to request if lits is refreshed
    //ie the page that holds the row the user was looking at
    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }



}
