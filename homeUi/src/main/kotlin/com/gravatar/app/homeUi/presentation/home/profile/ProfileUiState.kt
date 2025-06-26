package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField
import com.gravatar.restapi.models.Profile

internal data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val aboutFields: Set<AboutEditorField> = emptySet(),
)
