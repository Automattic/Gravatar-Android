package com.gravatar.app.usercomponent.data

import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.domain.model.UserSession
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

internal class InMemoryUserSessionPersistence(
    profileRepository: ProfileRepository,
    applicationScope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
) : UserSessionPersistence {

    private val _state: MutableSharedFlow<UserSession> = MutableSharedFlow(replay = 1)
    override val state: Flow<UserSession> = _state.distinctUntilChanged()

    init {
        applicationScope.launch(dispatcherProvider.io) {
            val initialSession = if (profileRepository.get().firstOrNull() != null) {
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
