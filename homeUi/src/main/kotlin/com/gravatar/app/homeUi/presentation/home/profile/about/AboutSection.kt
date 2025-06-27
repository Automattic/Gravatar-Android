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
    }
}

internal val AboutEditorField.labelRes: Int
    @StringRes get() = when (this.type) {
        AboutInputField.AboutMe -> R.string.about_field_label_about_me
        AboutInputField.DisplayName -> R.string.about_field_label_display_name
        AboutInputField.Location -> R.string.about_field_label_location
        AboutInputField.Pronouns -> R.string.about_field_label_pronouns
        AboutInputField.Pronunciation -> R.string.about_field_label_pronunciation
        AboutInputField.Company -> R.string.about_field_label_company
        AboutInputField.JobTitle -> R.string.about_field_label_job_title
        AboutInputField.FirstName -> R.string.about_field_label_first_name
        AboutInputField.LastName -> R.string.about_field_label_last_name
        else -> R.string.about_field_label_display_name
    }

internal val AboutEditorField.descriptionRes: Int?
    @StringRes get() = when (this.type) {
        else -> null
    }

@Preview(showBackground = true)
@Composable
internal fun AboutSectionPreview() {
    MaterialTheme {
        AboutSection(
            aboutFields = setOf(
                AboutEditorField(
                    type = AboutInputField.DisplayName,
                    value = "John Doe",
                    maxLines = 1,
                ),
                AboutEditorField(
                    type = AboutInputField.AboutMe,
                    value = "My description",
                    maxLines = 3,
                ),
                AboutEditorField(
                    type = AboutInputField.Pronunciation,
                    value = "John Doe",
                ),
                AboutEditorField(
                    type = AboutInputField.Pronouns,
                    value = "he/him",
                ),
                AboutEditorField(
                    type = AboutInputField.Location,
                    value = "San Francisco, CA",
                ),
                AboutEditorField(
                    type = AboutInputField.Company,
                    value = "Automattic",
                ),
                AboutEditorField(
                    type = AboutInputField.JobTitle,
                    value = "Software Engineer",
                ),
                AboutEditorField(
                    type = AboutInputField.FirstName,
                    value = "John",
                ),
                AboutEditorField(
                    type = AboutInputField.LastName,
                    value = "Doe",
                ),
            ),
            formEnabled = true,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
