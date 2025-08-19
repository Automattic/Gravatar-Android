package com.gravatar.app.homeUi.presentation.home.components.privacySetting

import com.gravatar.app.usercomponent.domain.model.PrivacySettings

internal data class PrivacySettingUiState(
    val privacySettings: PrivacySettings = PrivacySettings(
        analyticsEnabled = true,
        crashReportingEnabled = true
    )
)
