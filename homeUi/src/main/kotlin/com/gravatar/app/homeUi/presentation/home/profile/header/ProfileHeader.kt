package com.gravatar.app.homeUi.presentation.home.profile.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.AsyncImageWithCachePlaceholder
import com.gravatar.app.homeUi.presentation.home.components.GravatarAvatarWithShadow
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile

@Composable
internal fun ProfileHeader(
    profile: Profile,
    avatarUrl: String?,
    saveState: ProfileHeaderSaveState,
    onSaveProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        AsyncImageWithCachePlaceholder(
            avatarUrl.orEmpty(),
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 40.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
                .alpha(0.7f)
        )
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(16.dp)
                .systemBarsPadding()
        ) {
            GravatarAvatarWithShadow(
                url = avatarUrl.orEmpty(),
                borderShape = CircleShape,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(4f)) {
                BasicText(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = 18.sp
                    )
                )

                profile.jobInfo().takeIf { it.isNotBlank() }?.let { jobInfo ->
                    BasicText(
                        text = jobInfo,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(
                            maxFontSize = 14.sp
                        )
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                when (saveState) {
                    ProfileHeaderSaveState.SAVING -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }

                    ProfileHeaderSaveState.UNSAVED -> {
                        Text(
                            text = stringResource(R.string.profile_screen_save_button),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable { onSaveProfile.invoke() }
                        )
                    }

                    ProfileHeaderSaveState.SAVED -> {}
                }
            }
        }
    }
}

internal enum class ProfileHeaderSaveState {
    SAVED, UNSAVED, SAVING
}

private fun Profile.jobInfo(): String {
    return buildString {
        if (jobTitle.isNotBlank()) {
            append(jobTitle)
        }
        if (company.isNotBlank()) {
            if (isNotEmpty()) append(", ")
            append(company)
        }
    }
}

@Preview(showBackground = true, name = "Both job title and company")
@Composable
fun ProfileHeaderPreviewBoth() {
    ProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe",
            jobTitle = "Software Engineer",
            company = "Automattic"
        ),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.UNSAVED,
        onSaveProfile = {},
    )
}

@Preview(showBackground = true, name = "Only job title")
@Composable
fun ProfileHeaderPreviewJobOnly() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "Software Engineer", company = ""),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.SAVED,
        onSaveProfile = {},
    )
}

@Preview(showBackground = true, name = "Only company")
@Composable
fun ProfileHeaderPreviewCompanyOnly() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "", company = "Automattic"),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.SAVED,
        onSaveProfile = {},
    )
}

@Preview(showBackground = true, name = "Neither job title nor company")
@Composable
fun ProfileHeaderPreviewNeither() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "", company = ""),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.SAVED,
        onSaveProfile = {},
    )
}

@Preview(showBackground = true, name = "Long text with ellipsis")
@Composable
fun ProfileHeaderPreviewLongText() {
    ProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe with a very long name that should trigger ellipsis in the UI",
            jobTitle = "Senior Software Engineer with a very long title",
            company = "Automattic Inc. - A very long company name that should also trigger ellipsis"
        ),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.SAVED,
        onSaveProfile = {},
    )
}
