package com.gravatar.app.usercomponent.data

import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class InMemoryUserSessionPersistence(
    authRepository: AuthRepository,
    applicationScope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
) : UserSessionPersistence {

    private val _state: MutableSharedFlow<UserSession> = MutableSharedFlow(replay = 1)
    override val state: Flow<UserSession> = _state

    init {
        applicationScope.launch(dispatcherProvider.io) {
            val initialSession = if (authRepository.isUserLoggedIn()) {
                UserSession.LOGGED_IN
            } else {
                UserSession.LOGGED_OUT
            }
            _state.emit(initialSession)
        }
    }

    override suspend fun set(session: UserSession) {
        _state.emit(session)
    }
}

internal interface UserSessionPersistence {
    val state: Flow<UserSession>

    suspend fun set(session: UserSession)
}
