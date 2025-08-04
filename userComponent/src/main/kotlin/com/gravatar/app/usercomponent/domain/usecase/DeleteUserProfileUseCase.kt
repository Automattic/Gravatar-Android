package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.services.GravatarService
import com.gravatar.app.usercomponent.domain.repository.AuthRepository

internal class DeleteUserProfileUseCase(
    private val gravatarService: GravatarService,
    private val authRepository: AuthRepository,
    private val logout: Logout
) : DeleteUserProfile {

    override suspend fun invoke(): Result<Unit> {
        val token = authRepository.getToken()
        return if (token != null) {
            gravatarService.deleteProfile(token)
                .onSuccess {
                    logout()
                }
        } else {
            Result.failure(IllegalStateException("User is not logged in"))
        }
    }
}

interface DeleteUserProfile {
    suspend operator fun invoke(): Result<Unit>
}
