package com.pnc.jetpackcomposedemos.features.artists.presentation

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pnc.jetpackcomposedemos.features.artists.data.CountingArtistsDataSource
import com.pnc.jetpackcomposedemos.features.artists.domain.AsyncRepository
import com.pnc.jetpackcomposedemos.testutil.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.system.measureTimeMillis


class AsyncViewModelTest {

    @get:Rule
    val mainDispatcher = MainDispatcherRule()

@Test
fun `does not load artists until ten seconds have passed`() = runTest{
    val dataSource = CountingArtistsDataSource()
    val viewModel = AsyncViewModel(AsyncRepository(dataSource))

    val realMillies = measureTimeMillis {
        viewModel.loadArtists()
        advanceTimeBy(9_999)
    }
    println("real time is $realMillies ms, virtual time is $currentTime ms")

    assertEquals(0,dataSource.calls)

}

    @Test
    fun `loads artists after exactly ten seconds`() = runTest{
        val dataSource = CountingArtistsDataSource()
        val viewModel = AsyncViewModel(AsyncRepository(dataSource))

        viewModel.loadArtists()
        runCurrent() //start coroutine but only execute up to current time (which is 0 and will not advance automatically)


        assertEquals(0, dataSource.calls)
        assertEquals(0, currentTime)

        advanceTimeBy(9_999) //moves the clock forward but doenst run case
        runCurrent() // executes the tasks up to the currenttime
        assertEquals(0, dataSource.calls) //still 1 milli early

        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, dataSource.calls)

        assertEquals(10_000, currentTime)



    }




    @Test
    fun `eventually loads artists`() = runTest{
        val dataSource = CountingArtistsDataSource()
        val viewModel = AsyncViewModel(AsyncRepository(dataSource))
        viewModel.loadArtists()

        //suppose we know this is async and takes some amount of time, but we either dont know
        //how long or it may be a variable amount of time, and we dont care exactly how long, we just
        //need to wait until it finishes to make our assertions

        advanceUntilIdle() //tells dispatcher to fast forward execute everything in queue untill its idle

        assertEquals(1, dataSource.calls)
    }


    @Test
    fun `does not load artists if the view model is cleared during wait`() = runTest{
        val dataSource = CountingArtistsDataSource()
        val viewModel = AsyncViewModel(AsyncRepository(dataSource))
        viewModel.loadArtists()

        advanceTimeBy(5_000)
        viewModel.viewModelScope.cancel() // what happens when screen goes away
        advanceUntilIdle()
        assertEquals(0,dataSource.calls)

    }
}