package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField

internal sealed class ProfileEvent {
    data class OnProfileFieldUpdated(
        val aboutField: AboutEditorField,
    ) : ProfileEvent()

    object OnSaveClicked : ProfileEvent()

    object OnCancelClicked : ProfileEvent()

    object OnRefreshProfile : ProfileEvent()

    object OnProfileLinkClicked : ProfileEvent()

    object OnAboutAppClicked : ProfileEvent()

    object OnDismissAboutAppDialog : ProfileEvent()

    object OnPrivacySettingClicked : ProfileEvent()

    object OnPrivacySettingDismissed : ProfileEvent()
}
