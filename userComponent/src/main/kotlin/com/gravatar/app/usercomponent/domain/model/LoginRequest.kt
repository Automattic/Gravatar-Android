package com.gravatar.app.usercomponent.domain.model

data class LoginRequest(
    val code: String,
    val clientSecret: String,
    val redirectUri: String,
    val clientId: String
)
