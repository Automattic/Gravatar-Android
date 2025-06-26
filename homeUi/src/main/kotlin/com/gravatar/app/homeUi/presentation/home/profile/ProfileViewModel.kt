package com.gravatar.app.homeUi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileService: ProfileService) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // -> Remove the hardcoded username <-
        fetchProfile("hamorillo")
    }

    private fun fetchProfile(username: String) {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }
            when (val result = profileService.retrieveCatching(hashOrUsername = username)) {
                is GravatarResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, profile = result.value)
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, profile = null)
                    }
                }
            }
        }
    }
}
