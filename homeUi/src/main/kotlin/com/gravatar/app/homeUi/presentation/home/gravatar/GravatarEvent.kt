package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri

sealed class GravatarEvent {
    data object Refresh : GravatarEvent()
    data class OnAvatarSelected(val avatarId: String) : GravatarEvent()
    data class OnLocalImageSelected(val uri: Uri) : GravatarEvent()
    data class OnImageCropped(val uri: Uri) : GravatarEvent()
    data class OnFailedAvatarDismissed(val uri: Uri) : GravatarEvent()
    data object OnFailedAvatarDialogDismissed : GravatarEvent()
    data class OnFailedAvatarTapped(val uri: Uri) : GravatarEvent()
    data class OnDeleteAvatar(val avatarId: String) : GravatarEvent()
    data class OnDownloadAvatar(val avatarId: String) : GravatarEvent()
    data class OnShowDeleteConfirmation(val avatarId: String) : GravatarEvent()
    data object OnDismissDeleteConfirmation : GravatarEvent()
}
