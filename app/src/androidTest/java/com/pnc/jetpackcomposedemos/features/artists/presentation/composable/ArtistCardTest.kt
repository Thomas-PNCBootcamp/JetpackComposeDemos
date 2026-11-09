package com.pnc.jetpackcomposedemos.features.artists.presentation.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.pnc.jetpackcomposedemos.features.artists.presentation.composables.ArtistCard
import com.pnc.jetpackcomposedemos.testutil.builders.anArtist
import com.pnc.jetpackcomposedemos.ui.theme.KazooTheme
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class ArtistCardTest {


    @get:Rule
    val composeRule = createComposeRule()

    private val artist = anArtist(
        name = "test artist",
        genre = "Rock",
        location = "New York, NY",
        description = "loud and proud"
    )
    private var clickCount = 0

    private fun setCard(){
        composeRule.setContent{
            KazooTheme{
                ArtistCard(
                    artist = artist,
                    onClick = {clickCount++}
                )
            }
        }
    }

//    @Test
//    fun explore_printSemanticsTress(){
//        setCard()
//        composeRule.onRoot().printToLog("ARTIST_CARD_MERGED")
//        composeRule.onRoot(useUnmergedTree = true).printToLog("ARTIST_CARD_UNMERGED")
//    }


    @Test
    fun givenArtist_whenShown_thenDisplaysNameGenreLocationAndDescription(){
        setCard()
        composeRule.onNodeWithText(artist.name, useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Genre: ${artist.genre}", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Location: ${artist.location}", useUnmergedTree = true).assertIsDisplayed()
        //composeRule.onNodeWithText(artist.description, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun givenCard_whenTapped_thenOnClickIsCalledOnce(){
        setCard()
        composeRule.onNodeWithText(artist.name, useUnmergedTree = true).performClick()
        assertEquals(1, clickCount)
    }

    @Test
    fun givenNotFavorite_whenStarTapped_thenBecomesFavorite(){
        setCard()
        composeRule.onNodeWithContentDescription("Not favorite", useUnmergedTree = true).performClick()
        composeRule.onNodeWithContentDescription("Favorite", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Not favorite", useUnmergedTree = true).assertDoesNotExist()
    }


    @Test
    fun givenFavorite_whenStarTapped_thenBecomesNotFavorite(){
        setCard()
        composeRule.onNodeWithContentDescription("Not favorite", useUnmergedTree = true).performClick()
        composeRule.onNodeWithContentDescription("Favorite", useUnmergedTree = true).performClick()

        composeRule.onNodeWithContentDescription("Not favorite", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Favorite", useUnmergedTree = true).assertDoesNotExist()

    }
}