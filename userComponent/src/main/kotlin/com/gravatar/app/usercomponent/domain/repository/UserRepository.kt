package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import java.io.File

interface UserRepository {

    suspend fun selectAvatar(avatarId: String): Result<Unit>

    suspend fun getAvatars(): Result<List<Avatar>>

    suspend fun getProfile(): Result<Profile>

    suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<Profile>

    suspend fun uploadAvatar(avatarFile: File): GravatarResult<Avatar, ErrorType>
}
