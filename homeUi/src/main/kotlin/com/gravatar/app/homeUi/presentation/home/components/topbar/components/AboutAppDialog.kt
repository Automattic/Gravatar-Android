package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    viewModel: AboutAppDialogViewModel = koinViewModel()
) {
    val appVersion: AppVersion = koinInject()
    GravatarDialog(
        onDismissRequest = onDismissRequest,
        content = {
            AboutAppDialogContent(
                appVersion = appVersion.value,
                onDone = onDismissRequest,
                onEvent = viewModel::onEvent,
                modifier = Modifier
            )
        }
    )
}

@Composable
internal fun AboutAppDialogContent(
    appVersion: String,
    onDone: () -> Unit,
    onEvent: (AboutAppDialogEvent) -> Unit,
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
                text = stringResource(R.string.about_app_dialog_privacy_policy),
                modifier = Modifier.clickable {
                    context.openPrivacyPolicy()
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
                    .padding(top = 8.dp)
                    .clickable {
                        onEvent(AboutAppDialogEvent.OnDeleteAccount)
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

private fun Context.openPrivacyPolicy() = openUrlInApp(PRIVACY_POLICY_URL)

private fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}

private fun Context.openUrlInApp(url: String) =
    CustomTabsIntent.Builder().build().launchUrl(this, url.toUri())

private const val SUPPORT_URL = "support.gravatar.com"
private const val SUPPORT_EMAIL = "support@gravatar.com"
private const val TERMS_OF_SERVICE_URL = "https://wordpress.com/tos/"
private const val PRIVACY_POLICY_URL = "https://automattic.com/privacy/"

@Preview(showBackground = true)
@Composable
private fun AboutAppDialogContentPreview() {
    GravatarAppTheme {
        AboutAppDialogContent(
            appVersion = "0.0.1",
            onDone = { },
            onEvent = { _ -> },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
