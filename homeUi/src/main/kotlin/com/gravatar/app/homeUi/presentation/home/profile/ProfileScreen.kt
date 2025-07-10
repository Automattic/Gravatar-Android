package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutSection
import com.gravatar.app.homeUi.presentation.home.profile.header.ProfileHeader
import com.gravatar.app.homeUi.presentation.home.profile.header.ProfileHeaderSaveState
import com.gravatar.extensions.defaultProfile
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(profileEvent = event)
        }
    )
}

@Composable
internal fun ProfileScreen(uiState: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        Column {
            ProfileHeader(
                profile = uiState.profile,
                avatarUrl = uiState.avatarUrl,
                saveState = when {
                    uiState.isSavingProfile -> ProfileHeaderSaveState.SAVING
                    uiState.hasUnsavedChanges -> ProfileHeaderSaveState.UNSAVED
                    else -> ProfileHeaderSaveState.SAVED
                },
                onSaveProfile = { onEvent(ProfileEvent.OnSaveClicked) },
            )
            when {
                uiState.showLoading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                uiState.profile == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.profile_tab_unable_to_load_profile),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(R.string.profile_tab_unable_to_load_profile_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Button(
                            onClick = { onEvent(ProfileEvent.OnRefreshProfile) },
                        ) {
                            Text(
                                text = stringResource(R.string.retry_cta),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                else -> {
                    AboutSection(
                        aboutFields = uiState.aboutFields,
                        formEnabled = !uiState.isSavingProfile,
                        onValueChange = {
                            onEvent(ProfileEvent.OnProfileFieldUpdated(it))
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfileUiState(
            isLoading = false,
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "Software Engineer",
                company = "Automattic"
            ),
            editedAboutFields = mapOf(
                AboutInputField.DISPLAY_NAME to "John Doe",
            ),
        ),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun NullProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfileUiState(
            isLoading = false,
            profile = null,
            editedAboutFields = mapOf(
                AboutInputField.DISPLAY_NAME to "John Doe",
            ),
        ),
        onEvent = {}
    )
}
