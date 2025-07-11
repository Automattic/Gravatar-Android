package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.clock.AppClock
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import java.io.File

internal class UploadAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
    private val clock: AppClock
) : UploadUserAvatar {

    override suspend fun invoke(avatarFile: File): GravatarResult<Avatar, ErrorType> {
        val result = userRepository.uploadAvatar(avatarFile)
        if (result is GravatarResult.Success && result.value.selected == true) {
            avatarCacheBusterStorage.saveAvatarCacheBuster(clock.now().toString())
        }
        return result
    }
}

interface UploadUserAvatar {
    suspend operator fun invoke(avatarFile: File): GravatarResult<Avatar, ErrorType>
}
