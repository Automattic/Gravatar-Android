package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import java.io.File

internal class UploadAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
) : UploadUserAvatar {

    override suspend fun invoke(avatarFile: File): GravatarResult<Avatar, ErrorType> {
        val result = userRepository.uploadAvatar(avatarFile)
        if (result is GravatarResult.Success && result.value.selected == true) {
            avatarCacheBusterStorage.saveAvatarCacheBuster(result.value.imageId)
        }
        return result
    }
}

interface UploadUserAvatar {
    suspend operator fun invoke(avatarFile: File): GravatarResult<Avatar, ErrorType>
}
