package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.data.UserStorage
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository

internal class LogoutUseCase(
    private val profileRepository: ProfileRepository,
    private val userStorage: UserStorage,
    private val userSessionPersistence: UserSessionPersistence,
) : Logout {
    override suspend fun invoke() {
        userStorage.clear()
        profileRepository.delete()
        userSessionPersistence.set(UserSession.LOGGED_OUT)
    }
}

interface Logout {
    suspend operator fun invoke()
}
