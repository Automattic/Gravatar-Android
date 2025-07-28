package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.share.ShareFieldType
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile

@Composable
internal fun SharePublicContactInfo(
    profile: Profile,
    userSharePreferences: UserSharePreferences,
    onUserPreferenceChanged: (ShareFieldType) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = 16.dp
    Column(
        modifier = modifier
    ) {
        ShareSectionTitle(
            title = R.string.share_tab_public_info_title,
            rightIcon = R.drawable.gravatar,
            rightIconTint = MaterialTheme.colorScheme.primary,
            Modifier.padding(top = 8.dp, bottom = 28.dp, start = horizontalPadding, end = horizontalPadding)
        )
        profile.fullName?.let { fullName ->
            SharePublicRow(
                label = stringResource(R.string.share_tab_name_label),
                value = fullName,
                checked = userSharePreferences.name,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.Name(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }
        profile.location.takeIfNotEmpty()?.let { location ->
            SharePublicRow(
                label = stringResource(R.string.about_field_label_location),
                value = location,
                checked = userSharePreferences.location,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.Location(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }
        profile.jobTitle.takeIfNotEmpty()?.let { title ->
            SharePublicRow(
                label = stringResource(R.string.about_field_label_job_title),
                value = title,
                checked = userSharePreferences.title,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.Title(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }
        profile.company.takeIfNotEmpty()?.let { organization ->
            SharePublicRow(
                label = stringResource(R.string.about_field_label_company),
                value = organization,
                checked = userSharePreferences.organization,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.Organization(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }

        profile.description.takeIfNotEmpty()?.let { description ->
            SharePublicRow(
                label = stringResource(R.string.about_field_label_about_me),
                value = description,
                checked = userSharePreferences.description,
                singleLineValue = false,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.Description(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }

        profile.profileUrl.toString().takeIfNotEmpty()?.let { profileUrl ->
            SharePublicRow(
                label = stringResource(R.string.share_tab_profile_url_label),
                value = profileUrl,
                checked = userSharePreferences.profileUrl,
                onCheckedChange = { onUserPreferenceChanged(ShareFieldType.ProfileUrl(it)) },
                modifier = Modifier.padding(horizontal = horizontalPadding),
            )
            ItemDivider()
        }
    }
}

private val Profile.fullName: String?
    get() = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim().ifEmpty {
        null
    }

private fun String.takeIfNotEmpty(): String? = ifEmpty { null }

@Preview(showBackground = true)
@Composable
private fun SharePublicContactInfoPreview() {
    GravatarAppTheme {
        SharePublicContactInfo(
            profile = defaultProfile(hash = "hash"),
            userSharePreferences = UserSharePreferences(
                name = true,
                location = true,
                title = true,
                organization = true,
                description = true,
                profileUrl = true
            ),
            onUserPreferenceChanged = {},
        )
    }
}
