package com.pnc.jetpackcomposedemos.features.artists.domain

import com.pnc.jetpackcomposedemos.features.artists.data.FakeArtistRepository
import com.pnc.jetpackcomposedemos.testutil.builders.anArtist
import kotlinx.coroutines.test.runTest
import org.junit.Test
import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class GetAvailableArtistsUseCaseTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }
//    @Test
//    fun `toList never finishes on a flow that never completes`() = runTest(timeout = 2.seconds) {
//
//        val repo = FakeArtistRepository(listOf(anArtist()))
//        val result = GetAvailableArtistsUseCase(repo)().toList()
//
//    }


    //MARK: these test cases do work i just dont have dependency implemented


//    @Test
//    fun `emit cached artists filtered to available and sorted by name`() = runTest {
//        val repo = FakeArtistRepository(
//            listOf(
//                anArtist(id = 3, name = "Zed"),
//                anArtist(id = 2, name = "Bob"),
//                anArtist(id = 1, name = "Alice", isAvailable = false)
//            )
//        )
//
//        GetAvailableArtistsUseCase(repo)().test {
//            assertEquals(listOf("Bob", "Zed"), awaitItem().map { it.name })
//        } //turbine stops collecting the flow of data and also asserts that nothing is left unconsumed in the flow
//    }
//
//    @Test
//    fun `emits again when the underlying data changes`() = runTest{
//        val repo = FakeArtistRepository(listOf(anArtist(id=1, name = "Alice")))
//        GetAvailableArtistsUseCase(repo)().test{
//            assertEquals(listOf("Alice"), awaitItem().map{it.name})
//            repo.artists.value = listOf(anArtist(id=1, name = "Bob"),anArtist(id=2, name = "Alice"),anArtist(id=3, name = "Charlie"))
//
//            assertEquals(listOf("Bob", "Alice", "Charlie"), awaitItem().map{it.name})
//
//            repo.artists.value = listOf(anArtist(id=1, name = "Bob"),anArtist(id=2, name = "Alice",isAvailable = false))
//            assertEquals(emptyList<Artist>(),awaitItem())
//        }
//    }

//    @Test
//    fun `does not emit again when the same artists are pushed `() = runTest{
//        val repo = FakeArtistRepository(listOf(anArtist(id=1)))
//        GetAvailableArtistsUseCase(repo)().test{
//            skipItems(1) //dont care about initial emission
//
//            repo.artists.value = listOf(anArtist(id=1))//equal list, new instance
//
//            expectNoEvents()
//
//        }
//    }
//
//    @Test
//    fun `propogates a failure from the reposiory flow`()= runTest{
//        val failing = object:ArtistRepository by FakeArtistRepository(){
//            override fun observeArtists(): Flow<List<Artist>> = flow{
//                emit(listOf(anArtist(name ="Abe")))
//                throw IOException("DB CLOSEd")
//            }
//        }
//
//        GetAvailableArtistsUseCase(failing)().test{
//            assertEquals(listOf("Abe"), awaitItem().map{it.name})
//            assertEquals("db closed", awaitError().message)
//
//        }
//
//    }
//
//
//    @Test
//    fun `completes when the repository flow completes`() = runTest{
//        val finite = object: ArtistRepository by FakeArtistRepository(){
//            override fun observeArtists(): Flow<List<Artist>> = flowOf(listOf(anArtist()))
//        }
//
//        GetAvailableArtistsUseCase(finite)().test{
//            skipItems(1)
//            awaitComplete()
//
//        }
//
//
//    }

}