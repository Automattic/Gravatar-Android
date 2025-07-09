package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

internal class IsUserLoggedInUseCase(
    private val userSessionPersistence: UserSessionPersistence,
) : IsUserLoggedIn {
    override fun invoke(): Flow<UserSession> {
        return userSessionPersistence.state
    }
}

interface IsUserLoggedIn {
    operator fun invoke(): Flow<UserSession>
}
