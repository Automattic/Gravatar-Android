package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R

@Composable
internal fun SaveProfileHeader(
    saveState: SaveProfileHeaderState,
    onSaveProfile: () -> Unit,
    onCancelProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        when (saveState) {
            SaveProfileHeaderState.SAVING -> {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.profile_screen_saving),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            SaveProfileHeaderState.UNSAVED -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Cancel button
                    Button(
                        onClick = { onCancelProfile.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black.copy(alpha = 0.2f)
                        ),
                    ) {
                        Text(text = stringResource(R.string.profile_screen_cancel_button))
                    }

                    // Save button
                    Button(
                        onClick = { onSaveProfile.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                    ) {
                        Text(text = stringResource(R.string.profile_screen_save_button))
                    }
                }
            }
        }
    }
}

internal enum class SaveProfileHeaderState {
    UNSAVED, SAVING
}

@Preview(showBackground = true, name = "Unsaved state")
@Composable
fun ProfileHeaderPreviewUnsaved() {
    SaveProfileHeader(
        saveState = SaveProfileHeaderState.UNSAVED,
        onSaveProfile = {},
        onCancelProfile = {},
    )
}

@Preview(showBackground = true, name = "Saving state")
@Composable
fun ProfileHeaderPreviewSaving() {
    SaveProfileHeader(
        saveState = SaveProfileHeaderState.SAVING,
        onSaveProfile = {},
        onCancelProfile = {},
    )
}
