package com.gravatar.app.homeUi.presentation.home.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class ShareViewModel(
    private val userRepository: UserRepository,
    private val getAvatarUrl: GetAvatarUrl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    internal val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    init {
        collectProfile()
        collectAvatarUrl()
    }

    @Suppress("UnusedParameter")
    fun onEvent(shareEvent: ShareEvent) = Unit

    private fun collectAvatarUrl() {
        getAvatarUrl()
            .onEach { avatarUrl ->
                _uiState.update { currentState ->
                    currentState.copy(
                        avatarUrl = avatarUrl?.toString(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun collectProfile() {
        userRepository.getProfile()
            .onEach { profile ->
                _uiState.update { currentState ->
                    currentState.copy(
                        profile = profile,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
