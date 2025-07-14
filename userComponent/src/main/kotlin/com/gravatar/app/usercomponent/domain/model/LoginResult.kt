package com.gravatar.app.usercomponent.domain.model

sealed class LoginResult {
    data object Success : LoginResult()
    data object AuthenticationFailure : LoginResult()
    data object ProfileLoadFailure : LoginResult()
}
