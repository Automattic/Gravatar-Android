package com.gravatar.app.homeUi.presentation.home.share

internal sealed class ShareEvent {
    data class OnEmailValueChanged(val value: String) : ShareEvent()
    data class OnPhoneValueChanged(val value: String) : ShareEvent()
    data class OnUserSharePreferencesChanged(val shareFieldType: ShareFieldType) : ShareEvent()
    data object OnAboutAppClicked : ShareEvent()
    data object OnDismissAboutAppDialog : ShareEvent()
    data object OnPrivateInformationClicked : ShareEvent()
    data object OnDismissPrivateInformationDialog : ShareEvent()
    data object OnShareClick : ShareEvent()
}
