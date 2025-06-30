package com.gravatar.app.homeUi.presentation.home.profile.about

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R

@Composable
internal fun AboutSection(
    aboutFields: Set<AboutEditorField>,
    formEnabled: Boolean,
    onValueChange: (AboutEditorField) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (aboutFields.any { it.type.isName }) {
            AboutFieldsSection(
                label = stringResource(R.string.about_field_section_label_name),
                fields = aboutFields.filter { it.type.isName }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        if (aboutFields.any { it.type.isProfessional }) {
            AboutFieldsSection(
                label = stringResource(R.string.about_field_section_label_professional),
                fields = aboutFields.filter { it.type.isProfessional }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        if (aboutFields.any { it.type.isAbout }) {
            AboutFieldsSection(
                label = stringResource(R.string.about_field_section_label_about),
                fields = aboutFields.filter { it.type.isAbout }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
        if (aboutFields.any { it.type.isContact }) {
            AboutFieldsSection(
                label = stringResource(R.string.about_field_section_label_contact),
                fields = aboutFields.filter { it.type.isContact }.toSet(),
                formEnabled = formEnabled,
                onValueChange = onValueChange,
            )
        }
    }
}

internal val AboutEditorField.labelRes: Int
    @StringRes get() = when (this.type) {
        AboutInputField.ABOUT_ME -> R.string.about_field_label_about_me
        AboutInputField.DISPLAY_NAME -> R.string.about_field_label_display_name
        AboutInputField.LOCATION -> R.string.about_field_label_location
        AboutInputField.PRONOUNS -> R.string.about_field_label_pronouns
        AboutInputField.PRONUNCIATION -> R.string.about_field_label_pronunciation
        AboutInputField.COMPANY -> R.string.about_field_label_company
        AboutInputField.JOB_TITLE -> R.string.about_field_label_job_title
        AboutInputField.FIRST_NAME -> R.string.about_field_label_first_name
        AboutInputField.LAST_NAME -> R.string.about_field_label_last_name
        AboutInputField.CELL_PHONE -> R.string.about_field_label_cell_phone
        AboutInputField.CONTACT_EMAIL -> R.string.about_field_label_contact_email
    }

internal val AboutEditorField.descriptionRes: Int?
    @StringRes get() = when (this.type) {
        else -> null
    }

internal enum class Section {
    NAME,
    PROFESSIONAL,
    ABOUT,
    CONTACT,
}

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    MaterialTheme {
        AboutSection(
            aboutFields = setOf(
                AboutEditorField(
                    type = AboutInputField.DISPLAY_NAME,
                    value = "John Doe",
                    maxLines = 1,
                ),
                AboutEditorField(
                    type = AboutInputField.ABOUT_ME,
                    value = "My description",
                    maxLines = 3,
                ),
                AboutEditorField(
                    type = AboutInputField.PRONUNCIATION,
                    value = "John Doe",
                ),
                AboutEditorField(
                    type = AboutInputField.PRONOUNS,
                    value = "he/him",
                ),
                AboutEditorField(
                    type = AboutInputField.LOCATION,
                    value = "San Francisco, CA",
                ),
                AboutEditorField(
                    type = AboutInputField.COMPANY,
                    value = "Automattic",
                ),
                AboutEditorField(
                    type = AboutInputField.JOB_TITLE,
                    value = "Software Engineer",
                ),
                AboutEditorField(
                    type = AboutInputField.FIRST_NAME,
                    value = "John",
                ),
                AboutEditorField(
                    type = AboutInputField.LAST_NAME,
                    value = "Doe",
                ),
                AboutEditorField(
                    type = AboutInputField.CELL_PHONE,
                    value = "123-456-7890",
                ),
                AboutEditorField(
                    type = AboutInputField.CONTACT_EMAIL,
                    value = "gravatar@automattic.com",
                ),
            ),
            formEnabled = true,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
