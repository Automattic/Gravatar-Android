package com.gravatar.app.homeUi.presentation.home.components.topbar.components.about

sealed class AboutAppDialogEvent {
    data object OnShowDeleteConfirmation : AboutAppDialogEvent()
    data object OnHideDeleteConfirmation : AboutAppDialogEvent()
    data object OnConfirmDeleteAccount : AboutAppDialogEvent()
}
