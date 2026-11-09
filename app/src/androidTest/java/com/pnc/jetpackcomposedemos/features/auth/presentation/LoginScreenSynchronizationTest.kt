package com.pnc.jetpackcomposedemos.features.auth.presentation

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.pnc.jetpackcomposedemos.features.auth.domain.AuthRepository
import com.pnc.jetpackcomposedemos.features.auth.domain.LoginUseCase
import com.pnc.jetpackcomposedemos.features.auth.domain.User
import com.pnc.jetpackcomposedemos.testutil.builders.aUser
import com.pnc.jetpackcomposedemos.ui.theme.KazooTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
@OptIn(ExperimentalTestApi::class)

class LoginScreenSynchronizationTest {


    @get:Rule
    val composeRule = createComposeRule()

    private class SlowAuthRepository(
        private val delayMillis:Long,
        private val result:Result<User>
    ): AuthRepository{

        override suspend fun login(
            userId: String,
            passcode: String
        ): Result<User> {
            delay(delayMillis)
            return result
        }
    }

    private class GatedAuthRepository(): AuthRepository{

        val gate = CompletableDeferred<Result<User>>()

        override suspend fun login(
            userId: String,
            passcode: String
        ): Result<User> = gate.await()

    }

    private val loggedInUsers = mutableListOf<User>()

    private fun setLoginScreen(repository: AuthRepository){
        val viewModel = LoginViewModel(loginUseCase = LoginUseCase(repository))
        composeRule.setContent {
            KazooTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {loggedInUsers.add(it)}
                )
            }
        }
    }


    private val loadingIndicator = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)

    private fun enterCredentialsAndTapLogin(){
        composeRule.onNodeWithText("User ID").performTextInput("hello")
        composeRule.onNodeWithText("Passcode").performTextInput("world")
        composeRule.onNodeWithText("Login").performClick()


    }

//    @Test
//    fun givenSlowLogin_whenAssertedImmediately_thenFails(){
//        setLoginScreen(SlowAuthRepository(500,Result.success(aUser())))
//        enterCredentialsAndTapLogin()
//        composeRule.onNodeWithText("Welcome, Hello").assertIsDisplayed()
//    }

    @Test
    fun givenSlowLogin_whenLoginTapped_thenWelcomeAppearsOnceLoginCompletes(){
        setLoginScreen(SlowAuthRepository(500,Result.success(aUser(username="hello"))))
        enterCredentialsAndTapLogin()
        //compose cant know how long our repo may take
        composeRule.waitUntilExactlyOneExists(hasText("Welcome, hello"), timeoutMillis = 5000)
        composeRule.onNodeWithText("Welcome, hello").assertIsDisplayed()
    }


    @Test
    fun givenGatedLogin_whenLoginTapped_thenLoadingShowsUntilGateOpens(){
        val repository = GatedAuthRepository()
        setLoginScreen(repository)
        enterCredentialsAndTapLogin()

        //the repository is parked at the gate, so loading should be on the screen
        //and will stay there until we release the repository from the gate
        composeRule.onNode(loadingIndicator).assertIsDisplayed()
        composeRule.onNodeWithText("Welcome, hello").assertDoesNotExist()

        //once done with all assertions we want to run at this point
        repository.gate.complete(Result.success(aUser(username = "hello")))
        composeRule.waitUntilExactlyOneExists(hasText("Welcome, hello"), timeoutMillis = 5000)
        composeRule.onNode(loadingIndicator).assertDoesNotExist()


    }


}