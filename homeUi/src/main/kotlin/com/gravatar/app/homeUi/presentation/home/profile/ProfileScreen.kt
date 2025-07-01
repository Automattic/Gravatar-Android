package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutSection
import com.gravatar.app.homeUi.presentation.home.profile.header.ProfileHeader
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
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            uiState.profile.let { profile ->
                if (profile != null) {
                    Column {
                        Row {
                            ProfileHeader(
                                profile = profile,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(4f)
                            )

                            if (uiState.isSavingProfile) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 8.dp)
                                )
                            } else {
                                if (uiState.hasUnsavedChanges) {
                                    Text(
                                        text = stringResource(R.string.profile_screen_save_button),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(horizontal = 8.dp)
                                            .clickable {
                                                onEvent(ProfileEvent.OnSaveClicked)
                                            }
                                    )
                                }
                            }
                        }

                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            AboutSection(
                                aboutFields = uiState.aboutFields,
                                formEnabled = !uiState.isSavingProfile,
                                onValueChange = {
                                    onEvent(ProfileEvent.OnProfileFieldUpdated(it))
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Text("There was an error retrieving the profile.")
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
