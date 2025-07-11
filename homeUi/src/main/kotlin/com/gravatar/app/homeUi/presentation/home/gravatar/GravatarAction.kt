package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import java.io.File

sealed class GravatarAction {
    data class LaunchImageCropper(val imageUri: Uri, val tempFile: File) : GravatarAction()
    data object AvatarSelected : GravatarAction()
    data object AvatarSelectionFailed : GravatarAction()
    data object AvatarDeleted : GravatarAction()
    data object AvatarDeletionFailed : GravatarAction()
    data object DownloadManagerNotAvailable : GravatarAction()
    data object AvatarDownloadStarted : GravatarAction()
}
