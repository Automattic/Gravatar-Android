package com.gravatar.app.usercomponent.data

import com.gravatar.app.usercomponent.domain.model.OAuthRequest
import com.gravatar.app.usercomponent.domain.repository.AuthRepository

internal class RealAuthRepository(
    private val wordPressClient: WordPressClient,
    private val tokenStorage: AuthTokenStorage,
) : AuthRepository {
    override suspend fun fetchToken(oAuthRequest: OAuthRequest): Result<String> {
        return wordPressClient.login(
            code = oAuthRequest.code,
            clientSecret = oAuthRequest.clientSecret,
            redirectUri = oAuthRequest.redirectUri,
            clientId = oAuthRequest.clientId
        ).fold(
            onSuccess = { token ->
                tokenStorage.saveToken(token)
                Result.success(token)
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun getToken() = tokenStorage.getToken()
}
