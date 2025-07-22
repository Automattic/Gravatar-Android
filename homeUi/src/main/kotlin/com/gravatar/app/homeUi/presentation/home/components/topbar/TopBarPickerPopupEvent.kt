package com.gravatar.app.homeUi.presentation.home.components.topbar

sealed class TopBarPickerPopupEvent {
    data object OnLogoutSelected : TopBarPickerPopupEvent()
    data object OnProfileLinkClicked : TopBarPickerPopupEvent()
    data object OnGravatarLinkClicked : TopBarPickerPopupEvent()
    data object OnShareProfileClicked : TopBarPickerPopupEvent()
}
