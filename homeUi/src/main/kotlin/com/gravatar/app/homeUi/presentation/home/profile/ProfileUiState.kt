package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.restapi.models.Profile

internal data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val editedAboutFields: Map<AboutInputField, String> = emptyMap(),
) {

    val originalAboutFields: Set<AboutEditorField> = profile?.aboutFields() ?: emptySet()

    val aboutFields: Set<AboutEditorField> = originalAboutFields.map { field ->
        val editedValue = editedAboutFields[field.type]
        if (editedValue != null) {
            field.copy(value = editedValue)
        } else {
            field
        }
    }.toSet()

    val hasUnsavedChanges: Boolean = editedAboutFields.isNotEmpty()
}
