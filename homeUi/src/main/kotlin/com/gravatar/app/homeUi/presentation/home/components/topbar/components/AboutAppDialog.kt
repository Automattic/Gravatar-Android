package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.AppVersion
import com.gravatar.app.homeUi.R
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutAppDialog(
    onDismissRequest: () -> Unit,
) {
    val appVersion: AppVersion = koinInject()
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        content = {
            AboutAppDialogContent(
                appVersion = appVersion.value,
                onDone = onDismissRequest,
                modifier = Modifier
            )
        }
    )
}

@Composable
internal fun AboutAppDialogContent(
    appVersion: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .widthIn(max = MAX_DIALOG_WIDTH)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.about_app_dialog_about_gravatar),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = modifier.padding(top = 4.dp)
                )
                Text(
                    text = "v$appVersion",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                Text(
                    text = SUPPORT_URL,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        context.openSupportPage()
                    }
                )
                Text(
                    text = SUPPORT_EMAIL,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                Text(
                    text = stringResource(R.string.about_app_dialog_terms_of_service),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        context.openTermsOfService()
                    }
                )
                Text(
                    text = stringResource(R.string.about_app_dialog_privacy_policy),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        context.openPrivacyPolicy()
                    }
                )
            }
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = stringResource(R.string.done_button_cta),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

private fun Context.sendSupportEmail() {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:$SUPPORT_EMAIL".toUri()
    }
    startActivity(intent)
}

private fun Context.openSupportPage() = openUrl("https://$SUPPORT_URL")

private fun Context.openTermsOfService() = openUrl(TERMS_OF_SERVICE_URL)

private fun Context.openPrivacyPolicy() = openUrl(PRIVACY_POLICY_URL)

private fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    startActivity(intent)
}

private val MAX_DIALOG_WIDTH = 600.dp
private const val SUPPORT_URL = "support.gravatar.com"
private const val SUPPORT_EMAIL = "support@gravatar.com"
private const val TERMS_OF_SERVICE_URL = "https://wordpress.com/tos/"
private const val PRIVACY_POLICY_URL = "https://automattic.com/privacy/"

@Preview
@Composable
private fun AboutAppDialogContentPreview() {
    GravatarAppTheme {
        AboutAppDialogContent(
            appVersion = "0.0.1",
            onDone = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
