package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar

internal class FetchAvatarsUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
) : FetchUserAvatars {

    override suspend fun invoke(isRefreshing: Boolean): Result<List<Avatar>> {
        return userRepository.getAvatars()
            .onSuccess { avatars ->
                if (isRefreshing) {
                    val avatarID = avatars.firstOrNull { it.selected == true }?.imageId
                    avatarID?.let { id ->
                        avatarCacheBusterStorage.saveAvatarCacheBuster(id)
                    }
                }
            }
    }
}

interface FetchUserAvatars {
    suspend operator fun invoke(isRefreshing: Boolean): Result<List<Avatar>>
}
