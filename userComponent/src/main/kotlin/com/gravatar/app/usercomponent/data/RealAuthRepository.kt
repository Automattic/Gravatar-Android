package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.repository.AuthRepository

internal class RealAuthRepository(
    private val wordPressClient: WordPressClient,
    private val userStorage: UserStorage,
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<Unit> {
        return wordPressClient.login(
            code = loginRequest.code,
            clientSecret = loginRequest.clientSecret,
            redirectUri = loginRequest.redirectUri,
            clientId = loginRequest.clientId
        ).fold(
            onSuccess = { token ->
                userStorage.saveToken(token)
                Result.success(Unit)
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun isUserLoggedIn(): Boolean = userStorage.getToken() != null

    override suspend fun logout() {
        userStorage.clear()
    }
}
