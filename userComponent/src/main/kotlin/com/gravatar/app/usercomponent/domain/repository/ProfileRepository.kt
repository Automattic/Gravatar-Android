package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import kotlinx.coroutines.flow.Flow

internal interface ProfileRepository {

    suspend fun refreshUserProfile(): Result<Unit>

    fun get(): Flow<Profile?>

    suspend fun update(updateRequest: UpdateProfileRequest): Result<Unit>

    suspend fun delete()
}
