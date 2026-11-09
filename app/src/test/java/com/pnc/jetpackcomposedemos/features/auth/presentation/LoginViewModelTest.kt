package com.pnc.jetpackcomposedemos.features.auth.presentation

import com.pnc.jetpackcomposedemos.features.auth.domain.LoginUseCase
import com.pnc.jetpackcomposedemos.features.auth.domain.RecordingAuthRepository
import com.pnc.jetpackcomposedemos.testutil.MainDispatcherRule
import com.pnc.jetpackcomposedemos.testutil.builders.aUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    //when using the rule approach instead of #@before and after. do not create the view model in a class constructor or property init
    // those execute before the test starting method in our main dispatcher rule (create the view model inside the test or in its own before func




    @Test
    fun `given valid creds when logging in, then state is success `() = runTest{
        val user = aUser()
        val repo = RecordingAuthRepository(result = Result.success(user))

        val viewModel = LoginViewModel(LoginUseCase(repo))
        viewModel.login("hello", "world")

        advanceUntilIdle() //wait untill the coroutine launced by login is finished.

        assertEquals(LoginUiState.Success(user), viewModel.uiState.value)

    }
}