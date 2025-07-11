package com.gravatar.app.usercomponent.domain.repository

import com.gravatar.app.usercomponent.domain.model.OAuthRequest

internal interface AuthRepository {

    suspend fun fetchToken(oAuthRequest: OAuthRequest): Result<String>
}
