package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest

interface UserRepository {

    suspend fun selectAvatar(avatarId: String): Result<Unit>

    suspend fun getAvatars(): Result<List<Avatar>>

    suspend fun getProfile(): Result<Profile>

    suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<Profile>
}
