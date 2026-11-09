package com.pnc.jetpackcomposedemos.features.artists.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pnc.jetpackcomposedemos.core.room.AppDatabase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import com.pnc.jetpackcomposedemos.testutil.builders.anArtistEntity
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first

class ArtistDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ArtistDao


    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.artistDao()
    }

    @After
    fun closeDb(){
        db.close()
    }

    @Test
    fun givenEmptyTable_whenObserved_thenEmitsAnEmptyList() = runTest{
        dao.observeArtists().test{
            assertEquals(emptyList<ArtistEntity>(),awaitItem())
        }
    }


    @Test
    fun givenObservedList_whenArtistsAreInserted_thenANewListIsEmitted()=runTest{
        dao.observeArtists().test{
            assertEquals(emptyList<ArtistEntity>(),awaitItem())
            dao.upsertArtists(listOf(anArtistEntity(id=1),anArtistEntity(id=2)))
            val ids = awaitItem().map{it.id}.sorted()
            assertEquals(listOf(1,2),ids)


        }
    }

    @Test
    fun givenExistingArtist_whenUpsertedWithTheSameID_thenTheRowIsReplacedNotDuplicated()= runTest{
        dao.upsertArtists(listOf(anArtistEntity(id=1,name = "old name")))

        dao.upsertArtists(listOf(anArtistEntity(id=1,name = "new name")))

        val artists = dao.observeArtists().first()

        assertEquals(listOf("new name"), artists.map{it.name})
    }

    @Test
    fun givenExistingArtists_whenReplaceAll_thenUpdatesAndDeletesInOneStep() = runTest{
        dao.upsertArtists(listOf(
            anArtistEntity(id=1,name = "Abe"),
            anArtistEntity(id=2,name = "Bob"),
            anArtistEntity(id=3,name = "Carol")
        ))

        dao.replaceAll(listOf(
            anArtistEntity(id=1,name = "Abe updated"),
            anArtistEntity(id=4,name = "Tim")
        ))

        val artists = dao.observeArtists().first().sortedBy{it.id}
        //tests whether inserts and deletes happened properly

        assertEquals(listOf(1,4),artists.map{it.id})
    }


    @Test
    fun givenNoSuchArtist_whenObservedById_thenEmitsNull() = runTest{
        dao.observeArtist(42).test{
            assertNull(awaitItem())

        }
    }


    @Test
    fun givenExistingArtist_whenObservedById_thenEmitsTheArtist() = runTest{
        val artist = anArtistEntity(id=1,name = "Abe")
        dao.upsertArtists(listOf(artist))
        val result = dao.observeArtist(1).first()
        assertEquals(artist,result)
    }

    @Test
    fun givenObservedArtis_whenADifferentArtistIsInserted_thenTheSameValueIsEmittedAgain()=runTest{
        dao.upsertArtists(listOf(anArtistEntity(id = 1,name = "Abe")))
        //not just observing one item, an emission still happens when the data changes
        // even tho there's no change to the one specific artist
        dao.observeArtist(1).test{
            val first = awaitItem()

            dao.upsertArtists(listOf(anArtistEntity(id = 2,name = "Bob")))

            assertEquals(first,awaitItem())
        }
    }

}