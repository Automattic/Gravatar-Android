package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.restapi.models.Profile

internal data class ShareUiState(
    val profile: Profile? = null,
    val avatarUrl: String? = null,
    val isAboutAppDialogVisible: Boolean = false,
    val privateContactInfo: PrivateContactInfo = PrivateContactInfo(),
)

internal data class PrivateContactInfo(
    val emailValue: String = "",
    val isEmailShared: Boolean = false,
    val phoneValue: String = "",
    val isPhoneShared: Boolean = false,
)
