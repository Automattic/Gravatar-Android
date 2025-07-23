package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.restapi.models.Profile

internal data class ShareUiState(
    val profile: Profile? = null,
    val avatarUrl: String? = null,
    val emailValue: String = "",
    val isEmailShared: Boolean = false,
    val phoneValue: String = "",
    val isPhoneShared: Boolean = false
)
