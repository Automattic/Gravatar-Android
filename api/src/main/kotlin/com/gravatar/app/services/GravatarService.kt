package com.gravatar.app.services

import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.restapi.GravatarApi
import kotlinx.coroutines.withContext

internal class GravatarServiceImpl(
    private val gravatarApi: GravatarApi,
    private val dispatchers: DispatcherProvider
) : GravatarService {

    override suspend fun deleteProfile(authorization: String): Result<Unit> = withContext(dispatchers.io) {
        return@withContext try {
            val response = gravatarApi.disableAccount("Bearer $authorization")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to disable account."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface GravatarService {
    suspend fun deleteProfile(authorization: String): Result<Unit>
}