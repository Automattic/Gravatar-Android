package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField

internal sealed class ProfileEvent {
    data class OnProfileFieldUpdated(
        val aboutField: AboutEditorField,
    ) : ProfileEvent()

    object OnSaveClicked : ProfileEvent()

    object OnRefreshProfile : ProfileEvent()
}
