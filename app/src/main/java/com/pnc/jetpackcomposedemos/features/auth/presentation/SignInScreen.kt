package com.pnc.jetpackcomposedemos.features.auth.presentation

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pnc.jetpackcomposedemos.features.auth.domain.User
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.jetpackcomposedemos.R

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onSignIn:(User) ->Unit = {}
){
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current // used for opening the browser within app context


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Button(
            onClick = {
                val url = viewModel.startSignIn()
                CustomTabsIntent.Builder().build().launchUrl(context,url.toUri())
            }

        ){
            Text(stringResource(R.string.sign_in_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when(val current = state){
            is SignInState.Idle -> {}
            is SignInState.InBrowser -> Text(stringResource(R.string.sign_in_waiting))
            is SignInState.Exchanging -> CircularProgressIndicator()
            is SignInState.Success -> {
                Text(stringResource(R.string.common_welcome, current.user.username))
                LaunchedEffect(current.user){
                    onSignIn(current.user)
                }
            }
            is SignInState.Error -> Text(stringResource(R.string.common_error),color = MaterialTheme.colorScheme.error)

        }





    }



}