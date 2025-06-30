package com.gravatar.app.homeUi.presentation.home.gravatar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class GravatarViewModel(
    val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GravatarUiState())
    val uiState: StateFlow<GravatarUiState> = _uiState.asStateFlow()

    init {
        fetchAvatars(isRefreshing = false)
    }

    fun onEvent(event: GravatarEvent) {
        when (event) {
            GravatarEvent.Refresh -> fetchAvatars(isRefreshing = true)
            is GravatarEvent.OnAvatarSelected -> selectAvatar(event.avatarId)
        }
    }

    private fun selectAvatar(avatarId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectingAvatarId = avatarId)
            }
            userRepository.selectAvatar(avatarId)
                .onSuccess {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectingAvatarId = null,
                            selectedAvatarId = avatarId,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectingAvatarId = null,
                        )
                    }
                }
        }
    }

    private fun fetchAvatars(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = isRefreshing,
                    isLoading = !isRefreshing
                )
            }
            userRepository.getAvatars()
                .onSuccess { avatars ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            avatars = avatars,
                            selectedAvatarId = avatars.firstOrNull { it.selected == true }?.imageId,
                            isRefreshing = false,
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isRefreshing = false,
                            isLoading = false,
                        )
                    }
                }
        }
    }
}
