package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest

internal interface ProfileRepository {

    suspend fun refreshUserProfile(): Result<Unit>

    suspend fun get(): Result<Profile>

    suspend fun update(updateRequest: UpdateProfileRequest): Result<Profile>

    suspend fun delete()
}
