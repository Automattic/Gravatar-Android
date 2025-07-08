package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.types.Hash
import java.io.File

internal class RealUserRepository(
    private val avatarService: AvatarService,
    private val tokenStorage: AuthTokenStorage,
    private val profileRepository: ProfileRepository
) : UserRepository {

    override suspend fun selectAvatar(avatarId: String): Result<Unit> {
        val token = tokenStorage.getToken()
        return if (token != null) {
            val result = profileRepository.get()
                .getOrNull()
                ?.let { profile ->
                    avatarService.setAvatarCatching(
                        oauthToken = token,
                        hash = profile.hash,
                        avatarId = avatarId,
                    )
                }
            if (result is GravatarResult.Success) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to select avatar"))
            }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }

    override suspend fun getAvatars(): Result<List<Avatar>> {
        val token = tokenStorage.getToken()
        return if (token != null) {
            val avatars = profileRepository.get()
                .getOrNull()
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

    override suspend fun getProfile(): Result<Profile> {
        return profileRepository.get()
    }

    override suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<Profile> {
        return profileRepository.update(updateRequest)
    }

    override suspend fun uploadAvatar(avatarFile: File): GravatarResult<Avatar, ErrorType> {
        val token = tokenStorage.getToken()
        return if (token != null) {
            profileRepository.get()
                .getOrNull()
                ?.let { profile ->
                    avatarService.uploadCatching(
                        file = avatarFile,
                        oauthToken = token,
                        hash = Hash(profile.hash)
                    )
                }
                ?: GravatarResult.Failure(ErrorType.Server)
        } else {
            GravatarResult.Failure(ErrorType.Unauthorized)
        }
    }

    override suspend fun deleteAvatar(avatarId: String): Result<Unit> {
        val token = tokenStorage.getToken()
        return if (token != null) {
            when (val result = avatarService.deleteAvatarCatching(avatarId, token)) {
                is GravatarResult.Success -> {
                    Result.success(Unit)
                }

                is GravatarResult.Failure -> {
                    Result.failure(IllegalStateException("Failed to delete avatar: ${result.error}"))
                }
            }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }
}
