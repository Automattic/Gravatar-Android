package com.gravatar.app.loginUi.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.loginUi.presentation.oauth.OAuthConfig
import com.gravatar.app.loginUi.presentation.oauth.OAuthResult
import com.gravatar.app.usercomponent.domain.model.LoginRequest
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoginViewModel(
    private val authRepository: AuthRepository,
    private val oAuthConfig: OAuthConfig
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _actions = Channel<LoginAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OAuthResultReceived -> handleOAuthResult(event.result)
        }
    }

    private fun handleOAuthResult(result: OAuthResult) {
        when (result) {
            OAuthResult.Dismissed -> Unit
            is OAuthResult.Token -> login(result.token)
            OAuthResult.Error -> sendAction(LoginAction.ShowError)
        }
    }

    private fun login(token: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val loginRequest = LoginRequest(
                code = token,
                clientSecret = oAuthConfig.clientSecret,
                redirectUri = oAuthConfig.redirectUri,
                clientId = oAuthConfig.clientId
            )

            authRepository.login(loginRequest)
                .onSuccess {
                    sendAction(LoginAction.UserLoggedIn)
                }
                .onFailure { error ->
                    sendAction(LoginAction.ShowError)
                }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun sendAction(action: LoginAction) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            _actions.send(action)
        }
    }
}

internal data class LoginUiState(
    val isLoading: Boolean = false,
)

internal sealed class LoginEvent {
    data class OAuthResultReceived(val result: OAuthResult) : LoginEvent()
}

internal sealed class LoginAction {
    data object UserLoggedIn : LoginAction()
    data object ShowError : LoginAction()
}
