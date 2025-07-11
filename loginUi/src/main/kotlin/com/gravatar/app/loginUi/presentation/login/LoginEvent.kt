package com.gravatar.app.loginUi.presentation.login

import com.gravatar.app.loginUi.presentation.oauth.OAuthResult

internal sealed class LoginEvent {
    data class OAuthResultReceived(val result: OAuthResult) : LoginEvent()
    data object OnTryAnotherAccountClicked : LoginEvent()
    data object OnLoadProfileClicked : LoginEvent()
    data object OnLoginClicked : LoginEvent()
}
