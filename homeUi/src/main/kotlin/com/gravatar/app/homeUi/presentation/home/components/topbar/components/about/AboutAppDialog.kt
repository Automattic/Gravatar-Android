package com.gravatar.app.homeUi.presentation.home.components.topbar.components.about

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.gravatar.app.design.components.button.PrimaryButton
import com.gravatar.app.design.components.dialog.DialogText
import com.gravatar.app.design.components.dialog.DialogTitle
import com.gravatar.app.design.components.dialog.GravatarDialog
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.AppVersion
import com.gravatar.app.homeUi.R
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutAppDialog(
    onDismissRequest: () -> Unit,
    onPrivacySettingsClicked: () -> Unit,
    viewModel: AboutAppDialogViewModel = koinViewModel()
) {
    val appVersion: AppVersion = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    GravatarDialog(
        onDismissRequest = onDismissRequest,
        content = {
            if (uiState.isLoading) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        strokeWidth = 4.dp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                AboutAppDialogContent(
                    appVersion = appVersion.value,
                    onDone = onDismissRequest,
                    onPrivacySettingsClicked = onPrivacySettingsClicked,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                )
            }
            if (uiState.showDeleteAccountErrorAlert) {
                BasicAlertDialog(
                    onDismissRequest = { viewModel.dismissErrorMessage() }
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = stringResource(R.string.about_app_dialog_delete_profile_error_message))
                            Spacer(modifier = Modifier.height(24.dp))
                            TextButton(
                                onClick = { viewModel.dismissErrorMessage() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(text = stringResource(R.string.done_button_cta))
                            }
                        }
                    }
                }
            }
        }
    )

    if (uiState.isDeleteConfirmationVisible) {
        DeleteConfirmationBottomSheet(
            onDismiss = { viewModel.onEvent(AboutAppDialogEvent.OnHideDeleteConfirmation) },
            onConfirm = { viewModel.onEvent(AboutAppDialogEvent.OnConfirmDeleteAccount) }
        )
    }
}

@Composable
internal fun AboutAppDialogContent(
    appVersion: String,
    onDone: () -> Unit,
    onEvent: (AboutAppDialogEvent) -> Unit,
    onPrivacySettingsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            DialogTitle(
                title = stringResource(R.string.about_app_dialog_about_gravatar),
                modifier = modifier.padding(top = 4.dp)
            )
            DialogText(
                text = "v$appVersion",
            )
        }
        Column {
            Text(
                text = stringResource(R.string.about_app_dialog_get_help),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = modifier.padding(top = 4.dp),
            )
            DialogText(
                text = SUPPORT_URL,
                modifier = Modifier.clickable {
                    context.openSupportPage()
                }
            )
            DialogText(
                text = SUPPORT_EMAIL,
                modifier = Modifier.clickable {
                    context.sendSupportEmail()
                }
            )
        }
        Column {
            Text(
                text = stringResource(R.string.about_app_dialog_legal),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = modifier.padding(top = 4.dp),
            )
            DialogText(
                text = stringResource(R.string.about_app_dialog_terms_of_service),
                modifier = Modifier.clickable {
                    context.openTermsOfService()
                }
            )
            DialogText(
                text = stringResource(R.string.about_app_dialog_privacy_settings),
                modifier = Modifier.clickable {
                    onPrivacySettingsClicked()
                }
            )
        }
        Column {
            Text(
                text = stringResource(R.string.about_app_dialog_delete_account),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = modifier.padding(top = 4.dp),
            )
            DialogText(
                text = stringResource(R.string.about_app_dialog_delete_profile_description),
            )
            Text(
                text = stringResource(R.string.about_app_dialog_delete_account_button),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clickable {
                        onEvent(AboutAppDialogEvent.OnShowDeleteConfirmation)
                    }
            )
        }
        PrimaryButton(
            text = stringResource(R.string.done_button_cta),
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun Context.sendSupportEmail() {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:$SUPPORT_EMAIL".toUri()
    }
    startActivity(intent)
}

private fun Context.openSupportPage() = openUrl("https://$SUPPORT_URL")

private fun Context.openTermsOfService() = openUrlInApp(TERMS_OF_SERVICE_URL)

private fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}

private fun Context.openUrlInApp(url: String) =
    CustomTabsIntent.Builder().build().launchUrl(this, url.toUri())

private const val SUPPORT_URL = "support.gravatar.com"
private const val SUPPORT_EMAIL = "support@gravatar.com"
private const val TERMS_OF_SERVICE_URL = "https://wordpress.com/tos/"

@Preview(showBackground = true)
@Composable
private fun AboutAppDialogContentPreview() {
    GravatarAppTheme {
        AboutAppDialogContent(
            appVersion = "0.0.1",
            onDone = { },
            onEvent = { _ -> },
            onPrivacySettingsClicked = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
