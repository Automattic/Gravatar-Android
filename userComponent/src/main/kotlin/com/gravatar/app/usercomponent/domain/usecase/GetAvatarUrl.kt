package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.AvatarUrl
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.types.Hash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URL

internal class GetAvatarUrlUseCase(
    private val profileRepository: ProfileRepository,
    private val avatarCacheBusterStorage: AvatarCacheBusterStorage,
) : GetAvatarUrl {

    override fun invoke(): Flow<URL?> {
        return avatarCacheBusterStorage.state
            .map { cacheBuster ->
                val hash: String? = profileRepository.get()
                    .getOrNull()?.hash
                hash?.let {
                    AvatarUrl(
                        hash = Hash(it),
                    ).url(cacheBuster)
                }
            }
    }
}

interface GetAvatarUrl {
    operator fun invoke(): Flow<URL?>
}
