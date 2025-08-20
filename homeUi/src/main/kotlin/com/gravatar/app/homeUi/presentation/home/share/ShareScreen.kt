package com.gravatar.app.homeUi.presentation.home.share

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.design.components.Screen
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.GravatarFileProvider
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.AboutAppDialog
import com.gravatar.app.homeUi.presentation.home.share.components.ExpandedQrCode
import com.gravatar.app.homeUi.presentation.home.share.components.ItemDivider
import com.gravatar.app.homeUi.presentation.home.share.components.PrivateInformationDialog
import com.gravatar.app.homeUi.presentation.home.share.components.ShareHeader
import com.gravatar.app.homeUi.presentation.home.share.components.SharePrivateContactInfo
import com.gravatar.app.homeUi.presentation.home.share.components.SharePublicContactInfo
import com.gravatar.extensions.defaultProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Suppress("UnusedParameter")
@Composable
internal fun ShareScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModel: ShareViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner),
    snackbarHostState: SnackbarHostState,
    onShouldShowBottomBar: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    when (action) {
                        is ShareAction.ShareVCard -> {
                            shareVCardFile(action.vCardFile, context)
                        }

                        is ShareAction.ShowBottomBar -> {
                            onShouldShowBottomBar(action.show)
                        }
                    }
                }
            }
        }
    }

    Screen(
        screenName = "qr",
        appearanceLightStatusBars = false,
    ) {
        ShareScreen(
            uiState = uiState,
            onEvent = { event ->
                viewModel.onEvent(event)
            }
        )
    }
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
                vCardQrCodeData = uiState.vCardQrCodeData.exportToString(withPhoto = false),
                onShareClick = { onEvent(ShareEvent.OnShareClick) },
                onExpandQrCodeClick = { onEvent(ShareEvent.OnExpandQrCodeClick) },
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

    AnimatedVisibility(
        visible = uiState.isQrCodeExpanded,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
    ) {
        ExpandedQrCode(
            qrCodeData = uiState.vCardQrCodeData.exportToString(withPhoto = false),
            avatarUrl = uiState.avatarUrl.orEmpty(),
            onDismissRequest = {
                onEvent(ShareEvent.OnDismissExpandedQrCode)
            }
        )
    }
}

private fun shareVCardFile(
    vCardFile: File,
    context: Context,
) {
    val vCardFileUri = GravatarFileProvider.getFileUri(context, vCardFile)

    val intentShareFile = Intent(Intent.ACTION_SEND)

    // Use the correct MIME type for vCard files
    intentShareFile.type = "text/vcard"
    intentShareFile.putExtra(Intent.EXTRA_STREAM, vCardFileUri)

    context.startActivity(Intent.createChooser(intentShareFile, null))
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
