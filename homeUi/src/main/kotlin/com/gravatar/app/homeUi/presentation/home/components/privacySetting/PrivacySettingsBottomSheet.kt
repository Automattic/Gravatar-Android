@file:OptIn(ExperimentalMaterial3Api::class)

package com.gravatar.app.homeUi.presentation.home.components.privacySetting

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R
import org.koin.androidx.compose.koinViewModel

private const val PRIVACY_POLICY_URL = "https://automattic.com/privacy/"

@Composable
internal fun PrivacySettingsBottomSheet(
    onDismissRequest: () -> Unit
) {
    val topPadding = with(LocalDensity.current) {
        WindowInsets.safeContent.only(WindowInsetsSides.Top).getTop(LocalDensity.current).toDp()
    }
    val viewModel: PrivacySettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.padding(top = topPadding),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(Modifier.size(width = 32.dp, height = 4.dp))
            }
        }
    ) {
        PrivacySettings(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onDismissRequest = onDismissRequest,
        )
    }
}

@Composable
internal fun PrivacySettings(
    uiState: PrivacySettingUiState,
    onEvent: (PrivacySettingsEvent) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth(),
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = stringResource(R.string.privacy_settings_top_bar_title))
            },
            navigationIcon = {
                IconButton(
                    onClick = onDismissRequest
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close_button)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(26.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp, top = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            PrivacySettingsCard(
                settingTitle = stringResource(R.string.privacy_settings_share_analytics_data_tittle),
                settingIcon = R.drawable.ic_analytics_tracking,
                settingDescription = stringResource(R.string.privacy_settings_share_analytics_data_description),
                checked = uiState.privacySettings.analyticsEnabled,
                onCheckedChange = { onEvent(PrivacySettingsEvent.OnAnalyticsEnabledChanged(it)) },
                extraContent = {
                    Text(
                        text = stringResource(R.string.privacy_settings_privacy_policy),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable {
                                context.openPrivacyPolicy()
                            }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            PrivacySettingsCard(
                settingTitle = stringResource(R.string.privacy_settings_share_crash_reports_title),
                settingIcon = R.drawable.ic_crashlytics,
                settingDescription = stringResource(R.string.privacy_settings_share_crash_reports_description),
                checked = uiState.privacySettings.crashReportingEnabled,
                onCheckedChange = { onEvent(PrivacySettingsEvent.OnCrashReportingEnabledChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun Context.openPrivacyPolicy() {
    val intent = Intent(Intent.ACTION_VIEW, PRIVACY_POLICY_URL.toUri())
    startActivity(intent)
}

@Composable
private fun PrivacySettingsCard(
    settingTitle: String,
    @DrawableRes settingIcon: Int,
    settingDescription: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    extraContent: @Composable ColumnScope.() -> Unit = {},
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, shape)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(settingIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = settingTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
        HorizontalDivider(thickness = 1.dp)
        Text(
            text = settingDescription,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        extraContent()
    }
}

@Preview
@Composable
internal fun PrivacySettingsPreview() {
    GravatarAppTheme {
        PrivacySettings(
            uiState = PrivacySettingUiState(),
            onEvent = {},
            onDismissRequest = {}
        )
    }
}
