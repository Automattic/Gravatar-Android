package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.data.database.ProfileDao
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import com.gravatar.app.usercomponent.data.database.model.toEntity
import com.gravatar.app.usercomponent.data.database.model.toVerifiedAccount
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.services.ProfileService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealProfileRepository(
    private val profileService: ProfileService,
    private val tokenStorage: AuthTokenStorage,
    private val profileDao: ProfileDao,
) : ProfileRepository {

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

    override fun get(): Flow<Profile?> {
        return profileDao.getProfileWithVerifiedAccounts()
            .map { joined ->
                if (joined == null) return@map null
                val accounts: List<VerifiedAccount> = joined.verifiedAccounts.map { it.toVerifiedAccount() }
                joined.profile.toProfile(accounts)
            }
    }

    override suspend fun update(updateRequest: UpdateProfileRequest): Result<Unit> {
        val token = tokenStorage.getToken()
        return if (token != null) {
            val result = profileService.updateProfileCatching(token, updateRequest).valueOrNull()
            if (result != null) {
                storeProfile(result)
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to update profile"))
            }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }

    override suspend fun delete() {
        profileDao.delete()
    }

    private suspend fun fetchProfile(): Result<Profile> {
        val token = tokenStorage.getToken()
        if (token != null) {
            val fetchedProfile = profileService.retrieveAuthenticatedCatching(withToken = token).valueOrNull()
            return if (fetchedProfile != null) {
                storeProfile(fetchedProfile)
                Result.success(fetchedProfile)
            } else {
                Result.failure(IllegalStateException("Failed to fetch user profile"))
            }
        } else {
            return Result.failure(IllegalStateException("User is not logged in"))
        }
    }

    private suspend fun storeProfile(result: Profile) {
        val profileEntity = ProfileEntity.fromProfile(result)
        val accounts = result.verifiedAccounts.map { it.toEntity(profileEntity.userId) }
        profileDao.insertProfileWithVerifiedAccounts(profileEntity, accounts)
    }
}
