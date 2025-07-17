package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.types.Hash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.net.URL

internal class GetAvatarUrlUseCase(
    private val profileRepository: ProfileRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
) : GetAvatarUrl {

    companion object {
        private const val AVATAR_SIZE = 512
    }

    override fun invoke(): Flow<URL?> {
        return avatarCacheBusterStorage.getAvatarCacheBuster()
            .combine(
                profileRepository.get().map { it?.hash }.distinctUntilChanged()
            ) { cacheBuster, hash ->
                hash?.let {
                    AvatarUrl(
                        hash = Hash(it),
                        avatarQueryOptions = AvatarQueryOptions.Builder()
                            .setPreferredSize(AVATAR_SIZE)
                            .build()
                    ).url(cacheBuster)
                }
            }
    }
}

interface GetAvatarUrl {
    operator fun invoke(): Flow<URL?>
}
