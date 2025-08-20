package com.gravatar.app.homeUi.presentation.home.components.privacySetting

internal sealed class PrivacySettingsEvent {
    data class OnAnalyticsEnabledChanged(val enabled: Boolean) : PrivacySettingsEvent()
    data class OnCrashReportingEnabledChanged(val enabled: Boolean) : PrivacySettingsEvent()
}
