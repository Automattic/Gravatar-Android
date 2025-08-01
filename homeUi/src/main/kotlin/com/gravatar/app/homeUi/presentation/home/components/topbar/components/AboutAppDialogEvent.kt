package com.gravatar.app.homeUi.presentation.home.components.topbar.components

sealed class AboutAppDialogEvent {
    data object OnDeleteAccount : AboutAppDialogEvent()
}
