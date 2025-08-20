package com.gravatar.app.loginUi.presentation.login

import android.content.Context
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.design.components.Screen
import com.gravatar.app.design.components.snackbar.GravatarSnackbarHost
import com.gravatar.app.design.components.snackbar.SnackbarType
import com.gravatar.app.design.components.snackbar.showGravatarSnackbar
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.loginUi.R
import com.gravatar.app.loginUi.presentation.login.components.ErrorMessage
import com.gravatar.app.loginUi.presentation.login.components.ErrorTitle
import com.gravatar.app.loginUi.presentation.login.components.LoginButton
import com.gravatar.app.loginUi.presentation.oauth.OAuthResultContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen() {
    Screen(screenName = "login") {
        LoginScreen(
            viewModel = koinViewModel(),
        )
    }
}

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()

    val oAuthLauncher = rememberLauncherForActivityResult(OAuthResultContract()) { result ->
        viewModel.onEvent(LoginEvent.OAuthResultReceived(result))
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is LoginAction.ShowLoginError -> {
                            scope.launch {
                                snackbarHostState.showLoginErrorSnack(context)
                            }
                        }

                        LoginAction.StartOAuth -> oAuthLauncher.launch(Unit)
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            GravatarSnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        LoginScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GravatarLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (uiState.error) {
                    LoginError.AuthorizationDenied,
                    null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            if (uiState.error is LoginError.AuthorizationDenied) {
                                ErrorTitle(
                                    title = stringResource(R.string.login_authorization_denied_title),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                )
                            }
                            LoginButton(
                                text = stringResource(R.string.log_in),
                                isLoading = uiState.isLoading,
                                onClick = {
                                    onEvent(LoginEvent.OnLoginClicked)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                    is LoginError.ProfileLoadFailure -> {
                        Column {
                            ErrorTitle(
                                title = stringResource(R.string.login_profile_load_failure_title),
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            ErrorMessage(
                                message = stringResource(R.string.login_profile_load_failure_generic_message),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                            )
                            LoginButton(
                                text = stringResource(R.string.login_profile_load_failure_cta),
                                isLoading = uiState.isLoading,
                                onClick = { onEvent(LoginEvent.OnLoadProfileClicked) },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            TextButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    onEvent(LoginEvent.OnTryAnotherAccountClicked)
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.login_profile_load_failure_alternative_cta),
                                    modifier = Modifier
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GravatarLogo(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GravatarIcon()
                GravatarText()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                GravatarIcon()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GravatarText()
                }
            }
        }
    }
}

private suspend fun SnackbarHostState.showLoginErrorSnack(
    context: Context,
) {
    showGravatarSnackbar(
        message = context.getString(R.string.login_retrieving_token_failure_message),
        snackbarType = SnackbarType.Error,
    )
}

@Composable
private fun GravatarIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(com.gravatar.app.design.R.drawable.ic_gravatar),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .size(116.dp)
            .padding(end = 16.dp)
    )
}

@Composable
private fun GravatarText() {
    Text(
        text = stringResource(R.string.gravatar),
        style = MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Black
        ),
    )
    Text(
        text = stringResource(R.string.your_globally_recognized_avatar),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    GravatarAppTheme {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(isLoading = false)
        )
    }
}

@Preview
@Composable
private fun LoginScreenAuthDenied() {
    GravatarAppTheme {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(
                isLoading = false,
                error = LoginError.AuthorizationDenied,
            )
        )
    }
}

@Preview
@Composable
private fun LoginScreenProfileLoadFailure() {
    GravatarAppTheme {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(
                isLoading = false,
                error = LoginError.ProfileLoadFailure(
                    reason = LoginError.ProfileLoadFailure.Reason.GENERIC_ERROR
                ),
            )
        )
    }
}

@Preview(widthDp = 1000, heightDp = 400)
@Composable
private fun LoginScreenLandscapePreview() {
    GravatarAppTheme {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(isLoading = false)
        )
    }
}

@Preview
@Composable
private fun LoginScreenLoadingPreview() {
    GravatarAppTheme {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(isLoading = true)
        )
    }
}

@Preview
@Composable
private fun LoginScreenDarkPreview() {
    GravatarAppTheme(darkTheme = true) {
        LoginScreen(
            onEvent = { },
            uiState = LoginUiState(isLoading = false)
        )
    }
}
