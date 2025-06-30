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
                        currentState.copy(avatars = avatars)
                    }
                }
                .onFailure {
                }
            _uiState.update { currentState ->
                currentState.copy(
                    isRefreshing = false,
                    isLoading = false
                )
            }
        }
    }
}
