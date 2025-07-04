package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository

internal class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : Logout {
    override suspend fun invoke() {
        profileRepository.delete()
        authRepository.logout()
    }
}

interface Logout {
    suspend operator fun invoke()
}
