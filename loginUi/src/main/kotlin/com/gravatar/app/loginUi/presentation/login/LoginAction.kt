package com.gravatar.app.loginUi.presentation.login

internal sealed class LoginAction {
    data object ShowLoginError : LoginAction()
    data object StartOAuth : LoginAction()
}
