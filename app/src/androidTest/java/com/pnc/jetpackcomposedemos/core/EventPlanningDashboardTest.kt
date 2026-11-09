package com.pnc.jetpackcomposedemos.core

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.pnc.jetpackcomposedemos.ui.theme.KazooTheme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventPlanningDashboardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val calls = mutableListOf<String>()

    @Before
    fun setup(){
        //render the composable being tested
        composeRule.setContent{
            KazooTheme(){
                EventPlanningDashboard(
                    onViewArtists = {calls.add("artists")},
                    onViewOrders = {calls.add("orders")},
                    onViewBoardMembers = {calls.add("board members")},
                    onViewToDos = {calls.add("ToDos")},
                    onSignOut = {calls.add("sign out")},
                    onSettings = {calls.add("settings")}
                )
            }
        }
    }


    @Test
    fun givenDashboard_whenShown_thenDisplaysTitle(){
        composeRule.onNode(hasTestTag("dashboard-title")).assertIsDisplayed()

    }

    @Test
    fun givenDashboard_whenShown_thenDisplaysAllTwoButtons(){
        listOf("view-board-members-button", "sign-out-button").forEach{
            composeRule.onNode(hasTestTag(it)).assertIsDisplayed()
        }

    }

//    @Test
//    fun givenDashboard_whenViewArtistsTapped_thenOnViewArtistsIsCalled(){
//        composeRule.onNode(hasTestTag("view-artists-button")).performClick()
//        assertEquals(listOf("artists"),calls)
//    }

    @Test
    fun givenDashboard_whenEachButtonIsTapped_thenOnlyItsOwnCallbackIsCalled(){
        val expectation = mapOf(
            "view-board-members-button" to "board members",
            "sign-out-button" to "sign out"
        )

        expectation.forEach{(label,expectedCall) ->
            calls.clear()
            composeRule.onNode(hasTestTag(label)).performClick()
            assertEquals("tapping $label",listOf(expectedCall), calls)
        }
    }

    @Test
    fun givenDashboard_whenNothingTapped_thenNoCallbacksAreCalled(){
        composeRule.waitForIdle()
        assertEquals(emptyList<String>(),calls)
    }

}