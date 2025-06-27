package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RealAuthRepository(
    private val wordPressClient: WordPressClient,
    private val tokenStorage: AuthTokenStorage,
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<Unit> {
        return wordPressClient.login(
            code = loginRequest.code,
            clientSecret = loginRequest.clientSecret,
            redirectUri = loginRequest.redirectUri,
            clientId = loginRequest.clientId
        ).fold(
            onSuccess = { token ->
                tokenStorage.save(token)
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun isUserLoggedIn(): Flow<Boolean> = tokenStorage.get().map { it != null }

    override suspend fun logout() {
        tokenStorage.clear()
    }
}
