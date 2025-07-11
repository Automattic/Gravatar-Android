package com.gravatar.app.homeUi.presentation.home.profile

sealed class ProfileAction {
    data object ProfileSaved : ProfileAction()
    data object ProfileSaveFailed : ProfileAction()
}
