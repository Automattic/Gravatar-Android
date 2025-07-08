package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository

internal class SelectAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
) : SelectUserAvatar {

    override suspend fun invoke(avatarId: String): Result<Unit> {
        return userRepository.selectAvatar(avatarId)
            .onSuccess {
                avatarCacheBusterStorage.set(System.currentTimeMillis().toString())
            }
    }
}

interface SelectUserAvatar {
    suspend operator fun invoke(avatarId: String): Result<Unit>
}
