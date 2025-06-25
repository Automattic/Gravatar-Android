package com.gravatar.app.loginUi.presentation.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.loginUi.presentation.oauth.OAuthResultContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
) {
    LoginScreen(
        onLoggedIn = onLoggedIn,
        viewModel = koinViewModel(),
    )
}

@Composable
internal fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        LoginAction.UserLoggedIn -> {
                            onLoggedIn()
                        }

                        LoginAction.ShowError -> {
                            Toast.makeText(context, "Login error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    val oAuthLauncher = rememberLauncherForActivityResult(OAuthResultContract()) { result ->
        onEvent(LoginEvent.OAuthResultReceived(result))
    }

    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    else -> {
                        Column {
                            Text("Login Screen")
                            Button(
                                onClick = {
                                    oAuthLauncher.launch(Unit)
                                }
                            ) {
                                Text("Log In")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLoggedIn = { },
    )
}
