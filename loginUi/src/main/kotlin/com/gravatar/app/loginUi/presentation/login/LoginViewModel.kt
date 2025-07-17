package com.gravatar.app.loginUi.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.loginUi.presentation.oauth.OAuthConfig
import com.gravatar.app.loginUi.presentation.oauth.OAuthResult
import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.model.LoginResult
import com.gravatar.app.usercomponent.domain.model.OAuthRequest
import com.gravatar.app.usercomponent.domain.usecase.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoginViewModel(
    private val login: Login,
    private val oAuthConfig: OAuthConfig
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _actions = Channel<LoginAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OAuthResultReceived -> handleOAuthResult(event.result)
            LoginEvent.OnTryAnotherAccountClicked -> restartLogin()
            LoginEvent.OnLoadProfileClicked -> loginUser()
            LoginEvent.OnLoginClicked -> sendAction(LoginAction.StartOAuth)
        }
    }

    private fun restartLogin() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    error = null,
                    isLoading = false,
                )
            }
        }
    }

    private fun handleOAuthResult(result: OAuthResult) {
        when (result) {
            OAuthResult.Dismissed -> Unit
            OAuthResult.Error -> _uiState.update { currentState ->
                currentState.copy(error = LoginError.AuthorizationDenied)
            }

            is OAuthResult.Token -> loginUser(result.code)
        }
    }

    private fun loginUser(code: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val loginRequest = code?.let {
                LoginRequest.FullLogin(
                    request = OAuthRequest(
                        code = code,
                        clientSecret = oAuthConfig.clientSecret,
                        redirectUri = oAuthConfig.redirectUri,
                        clientId = oAuthConfig.clientId
                    )
                )
            } ?: LoginRequest.LoadProfile

            when (login(loginRequest)) {
                LoginResult.AuthenticationFailure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = null,
                        )
                    }
                    sendAction(LoginAction.ShowLoginError)
                }

                LoginResult.ProfileLoadFailure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            error = LoginError.ProfileLoadFailure(
                                reason = LoginError.ProfileLoadFailure.Reason.GENERIC_ERROR,
                            ),
                            isLoading = false
                        )
                    }
                }

                LoginResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                        )
                    }
                }
            }
        }
    }

    private fun sendAction(action: LoginAction) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _actions.send(action)
        }
    }
}
