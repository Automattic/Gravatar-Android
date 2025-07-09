package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository

internal class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val userSessionPersistence: UserSessionPersistence,
) : Logout {
    override suspend fun invoke() {
        profileRepository.delete()
        authRepository.logout()
        userSessionPersistence.set(UserSession.LOGGED_OUT)
    }
}

interface Logout {
    suspend operator fun invoke()
}
