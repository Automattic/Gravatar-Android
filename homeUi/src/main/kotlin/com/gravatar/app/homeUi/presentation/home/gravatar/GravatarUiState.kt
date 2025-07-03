package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType

internal data class GravatarUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectingAvatarId: String? = null,
    val selectedAvatarId: String? = null,
    val avatars: List<Avatar> = emptyList(),
    val uploadingAvatar: Uri? = null,
    val failedUploads: List<AvatarUploadFailure> = emptyList(),
    val failedUploadDialog: AvatarUploadFailure? = null,
) {
    val avatarsUi: List<AvatarUi> = buildList {
        addAll(
            failedUploads.reversed().map { localAvatar ->
                AvatarUi.Local(
                    uri = localAvatar.uri,
                    isLoading = false,
                )
            },
        )
        uploadingAvatar?.let {
            add(
                AvatarUi.Local(
                    uri = uploadingAvatar,
                    isLoading = true,
                ),
            )
        }
        avatars.forEach { avatar ->
            add(
                AvatarUi.Uploaded(
                    avatar = avatar,
                    isSelected = avatar.imageId == selectedAvatarId,
                    isLoading = avatar.imageId == selectingAvatarId,
                )
            )
        }
    }
}

internal sealed class AvatarUi(val avatarId: String) {
    data class Uploaded(
        val avatar: Avatar,
        val isSelected: Boolean,
        val isLoading: Boolean,
    ) : AvatarUi(avatar.imageId)

    data class Local(
        val uri: Uri,
        val isLoading: Boolean,
    ) : AvatarUi(uri.toString())
}

internal data class AvatarUploadFailure(
    val uri: Uri,
    val error: ErrorType?,
)
