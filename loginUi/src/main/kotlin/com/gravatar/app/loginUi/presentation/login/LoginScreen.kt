package com.gravatar.app.loginUi.presentation.login

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.loginUi.R
import com.gravatar.app.loginUi.presentation.oauth.OAuthResultContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen() {
    LoginScreen(
        viewModel = koinViewModel(),
    )
}

@Composable
internal fun LoginScreen(
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

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        else -> {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !uiState.isLoading,
                                onClick = {
                                    oAuthLauncher.launch(Unit)
                                }
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        strokeWidth = 4.dp,
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.log_in),
                                        modifier = Modifier
                                            .padding(12.dp),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
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
