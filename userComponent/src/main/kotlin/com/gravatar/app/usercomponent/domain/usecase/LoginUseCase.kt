package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository

interface Login {
    suspend operator fun invoke(loginRequest: LoginRequest): Result<Unit>
}

internal class LoginUseCase(
    val authRepository: AuthRepository,
    val profileRepository: ProfileRepository,
) : Login {

    override suspend fun invoke(loginRequest: LoginRequest): Result<Unit> {
        return authRepository.login(loginRequest)
            .onSuccess {
                profileRepository.refreshUserProfile()
            }
    }
}
