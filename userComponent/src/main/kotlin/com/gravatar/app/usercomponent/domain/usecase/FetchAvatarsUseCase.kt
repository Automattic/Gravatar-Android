package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar

internal class FetchAvatarsUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
    private val clock: AppClock,
) : FetchUserAvatars {

    override suspend fun invoke(): Result<List<Avatar>> {
        return userRepository.getAvatars()
            .onSuccess { avatars ->
                val selectedAvatarId = avatars.firstOrNull { it.selected == true }?.imageId
                avatarCacheBusterStorage.saveAvatarCacheBuster(
                    selectedAvatarId ?: clock.now().toString()
                )
            }
    }
}

interface FetchUserAvatars {
    suspend operator fun invoke(): Result<List<Avatar>>
}
