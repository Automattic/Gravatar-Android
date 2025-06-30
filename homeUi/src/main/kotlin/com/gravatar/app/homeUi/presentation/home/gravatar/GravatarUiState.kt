package com.gravatar.app.homeUi.presentation.home.gravatar

import com.gravatar.restapi.models.Avatar

internal data class GravatarUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectingAvatarId: String? = null,
    val selectedAvatarId: String? = null,
    val avatars: List<Avatar> = emptyList()
) {
    val avatarsUi: List<AvatarUi> = avatars.map { avatar ->
        AvatarUi.Uploaded(
            avatar = avatar,
            isSelected = avatar.imageId == selectedAvatarId,
            isLoading = avatar.imageId == selectingAvatarId,
        )
    }
}

internal sealed class AvatarUi(val avatarId: String) {
    data class Uploaded(
        val avatar: Avatar,
        val isSelected: Boolean,
        val isLoading: Boolean,
    ) : AvatarUi(avatar.imageId)
}
