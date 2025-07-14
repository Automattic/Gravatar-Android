package com.gravatar.app.loginUi.presentation.login

internal data class LoginUiState(
    val isLoading: Boolean = false,
    val error: LoginError? = null,
)

internal sealed class LoginError {
    data object AuthorizationDenied : LoginError()
    data class ProfileLoadFailure(
        val reason: Reason,
    ) : LoginError() {
        enum class Reason {
            GENERIC_ERROR,
        }
    }
}
