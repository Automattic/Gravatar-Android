package com.gravatar.app.homeUi.presentation.home.share

internal sealed class ShareEvent {
    data class OnEmailValueChanged(val value: String) : ShareEvent()
    data class OnEmailSharingChanged(val isShared: Boolean) : ShareEvent()
    data class OnPhoneValueChanged(val value: String) : ShareEvent()
    data class OnPhoneSharingChanged(val isShared: Boolean) : ShareEvent()
    data object OnAboutAppClicked : ShareEvent()
    data object OnDismissAboutAppDialog : ShareEvent()
}
