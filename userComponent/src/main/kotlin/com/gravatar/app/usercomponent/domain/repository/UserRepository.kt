package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserRepository {

    suspend fun refreshProfile(): Result<Unit>

    suspend fun selectAvatar(avatarId: String): Result<Unit>

    suspend fun getAvatars(): Result<List<Avatar>>

    fun getProfile(): Flow<Profile?>

    suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<Unit>

    suspend fun uploadAvatar(avatarFile: File): GravatarResult<Avatar, ErrorType>

    suspend fun deleteAvatar(avatarId: String): Result<Unit>
}
