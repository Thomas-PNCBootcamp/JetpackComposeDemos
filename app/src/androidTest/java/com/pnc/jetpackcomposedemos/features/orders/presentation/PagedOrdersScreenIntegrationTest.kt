package com.pnc.jetpackcomposedemos.features.orders.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.pnc.jetpackcomposedemos.HiltTestActivity
import com.pnc.jetpackcomposedemos.features.auth.domain.SessionManager
import com.pnc.jetpackcomposedemos.features.auth.domain.User
import com.pnc.jetpackcomposedemos.testutil.builders.aMyOrdersResponse
import com.pnc.jetpackcomposedemos.testutil.fakes.FakeMyOrdersDataSource
import com.pnc.jetpackcomposedemos.ui.theme.KazooTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject


@HiltAndroidTest


class PagedOrdersScreenIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()


    @Inject
    lateinit var session: SessionManager

    @Inject
    lateinit var ordersApi: FakeMyOrdersDataSource

    @Before
    fun setup(){
        hiltRule.inject()
    }

    private fun showScreen(){
        composeRule.setContent{
            KazooTheme{
                PagedOrdersScreen(useTwoPaneLayout = false)
            }
        }
    }

    private fun signInAs(id:String = "11000",name:String = "Cara Cole"){
        session.signIn(User(id = id, username = name,token = "test-token"))
    }


    //paging loads data on a background thread which compose cant see
    private fun waitForText(text:String){
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text, useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }


    private fun httpError(code:Int) = HttpException(
        Response.error<Any>(code, "".toResponseBody("application/json".toMediaType()))
    )

//    @Test
//    fun givenSignedInCustomerWithOrders_whenScreenOpens_thenTheOrdersAreListed(){
//        signInAs()
//        ordersApi.response = aMyOrdersResponse(
//            11000,
//            aMyOrder(orderNumber = 1001, lines = listOf(aMyOrderLine(productName = "road bike")))
//            aMyOrder(orderNumber = 1002, lines = listOf(aMyOrderLine(lineId = 2, productName = "Helmet")))
//        )
//
//        showScreen()
//        waitForText("Order #:1001")
//        composeRule.onNodeWithText("Order #:1002").assertIsDisplayed()
//        composeRule.onNodeWithText("road bike").assertIsDisplayed()
//        composeRule.onNodeWithText("Helmet").assertIsDisplayed()
//
//    }

    @Test
    fun givenAnExpiration_whenScreenOpens_thenTheErrorIsDisplayed(){
        signInAs()
        ordersApi.failure = httpError(401)

        showScreen()
        waitForText("error: your session has expired please sign in again")
        composeRule.onNodeWithText("retry").assertIsDisplayed()
    }


}