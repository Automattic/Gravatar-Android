package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.clock.AppClock
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository

internal class DeleteUserAvatarUseCase(
    private val userRepository: UserRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
    private val clock: AppClock,
) : DeleteUserAvatar {

    override suspend fun invoke(avatarId: String, isSelected: Boolean): Result<Unit> {
        return userRepository.deleteAvatar(avatarId)
            .onSuccess {
                if (isSelected) {
                    avatarCacheBusterStorage.saveAvatarCacheBuster(clock.now().toString())
                }
            }
    }

}

interface DeleteUserAvatar {
    suspend operator fun invoke(avatarId: String, isSelected: Boolean): Result<Unit>
}
