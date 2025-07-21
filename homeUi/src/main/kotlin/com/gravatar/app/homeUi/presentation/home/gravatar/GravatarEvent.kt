package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri

sealed class GravatarEvent {
    data class Refresh(val pullToRefresh: Boolean = true) : GravatarEvent()
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
    data object OnLogoutSelected : GravatarEvent()
    data object OnProfileLinkClicked : GravatarEvent()
    data object OnGravatarLinkClicked : GravatarEvent()
    data object OnShareProfileClicked : GravatarEvent()
}
