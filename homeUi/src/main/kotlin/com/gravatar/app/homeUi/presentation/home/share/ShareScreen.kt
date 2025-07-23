package com.gravatar.app.homeUi.presentation.home.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelStoreOwner
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.presentation.home.share.components.ShareHeader
import org.koin.androidx.compose.koinViewModel

@Suppress("UnusedParameter")
@Composable
internal fun ShareScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModel: ShareViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner),
    snackbarHostState: SnackbarHostState
) {
    val uiState by viewModel.uiState.collectAsState()

    ShareScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

@Suppress("UnusedParameter")
@Composable
internal fun ShareScreen(uiState: ShareUiState, onEvent: (ShareEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ShareHeader(
            avatarUrl = uiState.avatarUrl.orEmpty(),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ShareScreenPreview() {
    GravatarAppTheme {
        ShareScreen(
            uiState = ShareUiState(),
            onEvent = { }
        )
    }
}
