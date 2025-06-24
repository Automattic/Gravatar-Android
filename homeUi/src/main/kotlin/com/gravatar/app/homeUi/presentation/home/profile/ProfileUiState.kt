package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.restapi.models.Profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
)
