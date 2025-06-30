package com.gravatar.app.homeUi.presentation.home.gravatar

sealed class GravatarEvent {
    data object Refresh : GravatarEvent()
}
