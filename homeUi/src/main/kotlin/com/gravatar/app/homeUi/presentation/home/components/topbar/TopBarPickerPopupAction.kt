package com.gravatar.app.homeUi.presentation.home.components.topbar

sealed class TopBarPickerPopupAction {
    data class OpenExternalUrl(val url: String) : TopBarPickerPopupAction()
    data class ShareProfileUrl(val url: String) : TopBarPickerPopupAction()
}
