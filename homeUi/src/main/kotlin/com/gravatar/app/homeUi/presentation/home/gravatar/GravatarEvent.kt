package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri

sealed class GravatarEvent {
    data object Refresh : GravatarEvent()
    data class OnAvatarSelected(val avatarId: String) : GravatarEvent()
    data class OnLocalImageSelected(val uri: Uri) : GravatarEvent()
    data class OnImageCropped(val uri: Uri) : GravatarEvent()
}
