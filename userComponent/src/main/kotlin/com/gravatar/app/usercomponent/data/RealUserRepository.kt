package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import com.gravatar.types.Hash
import kotlinx.coroutines.flow.first

internal class RealUserRepository(
    private val avatarService: AvatarService,
    private val profileService: ProfileService,
    private val tokenStorage: AuthTokenStorage,
) : UserRepository {

    override suspend fun getAvatars(): Result<List<Avatar>> {
        val token = tokenStorage.get().first()
        return if (token != null) {
            val avatars = profileService.retrieveAuthenticatedCatching(token)
                .valueOrNull()
                ?.let { profile ->
                    avatarService.retrieveCatching(
                        oauthToken = token,
                        hash = Hash(profile.hash)
                    ).valueOrNull()
                }
            if (avatars != null) {
                Result.success(avatars)
            } else {
                Result.failure(IllegalStateException("Failed to retrieve avatars"))
            }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }
}
