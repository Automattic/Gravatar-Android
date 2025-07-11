package com.gravatar.app.usercomponent.domain.model

sealed class LoginRequest {
    data class FullLogin(
        val request: OAuthRequest
    ) : LoginRequest()

    data object LoadProfile : LoginRequest()
}

data class OAuthRequest(
    val code: String,
    val clientSecret: String,
    val redirectUri: String,
    val clientId: String
)
