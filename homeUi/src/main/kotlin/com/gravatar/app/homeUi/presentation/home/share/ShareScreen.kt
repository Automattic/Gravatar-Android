package com.gravatar.app.homeUi.presentation.home.share

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.AboutAppDialog
import com.gravatar.app.homeUi.presentation.home.share.components.ItemDivider
import com.gravatar.app.homeUi.presentation.home.share.components.PrivateInformationDialog
import com.gravatar.app.homeUi.presentation.home.share.components.ShareHeader
import com.gravatar.app.homeUi.presentation.home.share.components.SharePrivateContactInfo
import com.gravatar.app.homeUi.presentation.home.share.components.SharePublicContactInfo
import com.gravatar.extensions.defaultProfile
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

@Composable
internal fun ShareScreen(uiState: ShareUiState, onEvent: (ShareEvent) -> Unit) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            ShareHeader(
                avatarUrl = uiState.avatarUrl.orEmpty(),
                onAboutAppClicked = {
                    onEvent(ShareEvent.OnAboutAppClicked)
                },
                vCardQrCodeData = uiState.vCardQrCodeData,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            SharePrivateContactInfo(
                privateContactState = uiState.privateContactState,
                onEmailValueChange = { onEvent(ShareEvent.OnEmailValueChanged(it)) },
                onEmailSwitchCheckedChange = {
                    onEvent(ShareEvent.OnUserSharePreferencesChanged(ShareFieldType.PrivateEmail(it)))
                },
                onPhoneValueChange = { onEvent(ShareEvent.OnPhoneValueChanged(it)) },
                onPhoneSwitchCheckedChange = {
                    onEvent(ShareEvent.OnUserSharePreferencesChanged(ShareFieldType.PrivatePhone(it)))
                },
                onTitleClicked = { onEvent(ShareEvent.OnPrivateInformationClicked) },
                modifier = Modifier.padding(16.dp),
            )
            ItemDivider()
            uiState.profile?.let { profile ->
                SharePublicContactInfo(
                    profile = profile,
                    userSharePreferences = uiState.userSharePreferences,
                    onUserPreferenceChanged = { userSharePreferences ->
                        onEvent(ShareEvent.OnUserSharePreferencesChanged(userSharePreferences))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }

    if (uiState.isAboutAppDialogVisible) {
        AboutAppDialog(
            onDismissRequest = {
                onEvent(ShareEvent.OnDismissAboutAppDialog)
            }
        )
    }

    if (uiState.isPrivateInformationDialogVisible) {
        PrivateInformationDialog(
            onDismissRequest = {
                onEvent(ShareEvent.OnDismissPrivateInformationDialog)
            }
        )
    }
}

@Preview
@Composable
private fun ShareScreenPreview() {
    GravatarAppTheme {
        ShareScreen(
            uiState = ShareUiState(
                profile = defaultProfile(
                    hash = ""
                ),
                isAboutAppDialogVisible = false,
                isPrivateInformationDialogVisible = false
            ),
            onEvent = { }
        )
    }
}
