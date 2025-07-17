package com.gravatar.app.homeUi.presentation.home.profile

sealed class ProfileAction {
    data object ProfileSaved : ProfileAction()
    data object ProfileSaveFailed : ProfileAction()
    data class OpenProfileUrl(val url: String) : ProfileAction()
}
