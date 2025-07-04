package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ProfileService
import kotlinx.coroutines.flow.firstOrNull

internal class RealProfileRepository(
    private val profileService: ProfileService,
    private val tokenStorage: AuthTokenStorage,
) : ProfileRepository {

    // Temporary in-memory storage for the profile
    private var profile: Profile? = null

    override suspend fun refreshUserProfile(): Result<Unit> {
        return fetchProfile().fold(
            onSuccess = {
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    override suspend fun get(): Result<Profile> {
        return if (profile != null) {
            Result.success(profile!!)
        } else {
            fetchProfile()
        }
    }

    override suspend fun update(updateRequest: UpdateProfileRequest): Result<Profile> {
        val token = tokenStorage.get().firstOrNull()
        return if (token != null) {
            val result = profileService.updateProfileCatching(token, updateRequest).valueOrNull()
            if (result != null) {
                profile = result
                Result.success(result)
            } else {
                Result.failure(IllegalStateException("Failed to update profile"))
            }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }

    override suspend fun delete() {
        profile = null
    }

    private suspend fun fetchProfile(): Result<Profile> {
        val token = tokenStorage.get().firstOrNull()
        if (token != null) {
            val fetchedProfile = profileService.retrieveAuthenticatedCatching(withToken = token).valueOrNull()
            return if (fetchedProfile != null) {
                profile = fetchedProfile
                Result.success(fetchedProfile)
            } else {
                Result.failure(IllegalStateException("Failed to fetch user profile"))
            }
        } else {
            return Result.failure(IllegalStateException("User is not logged in"))
        }
    }
}
