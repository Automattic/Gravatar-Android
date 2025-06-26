package com.gravatar.app.homeUi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.restapi.models.Profile
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileService: ProfileService) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    internal val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // -> Remove the hardcoded username <-
        fetchProfile("hamorillo")
        profileObserver()
    }

    private fun fetchProfile(username: String) {
        viewModelScope.launch {
            _uiState.update { currentState -> currentState.copy(isLoading = true) }
            when (val result = profileService.retrieveCatching(hashOrUsername = username)) {
                is GravatarResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            profile = result.value,
                        )
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

    private fun profileObserver() {
        uiState
            .distinctUntilChanged { old, new -> old.profile == new.profile }
            .onEach {
                _uiState.update { currentState ->
                    currentState.copy(aboutFields = currentState.profile?.aboutFields() ?: emptySet())
                }
            }
            .launchIn(viewModelScope)
    }
}

internal fun Profile.aboutFields(): Set<AboutEditorField> {
    return AboutInputField.all
        .map {
            AboutEditorField(
                type = it,
                value = when (it) {
                    AboutInputField.DisplayName -> displayName
                    AboutInputField.AboutMe -> description
                    AboutInputField.Pronouns -> pronouns
                    AboutInputField.Pronunciation -> pronunciation
                    AboutInputField.Location -> location
                    AboutInputField.JobTitle -> jobTitle
                    AboutInputField.Company -> company
                    AboutInputField.FirstName -> firstName.orEmpty()
                    AboutInputField.LastName -> lastName.orEmpty()
                    else -> ""
                },
                maxLines = when (it) {
                    AboutInputField.AboutMe -> 4
                    else -> 1
                },
            )
        }
        .sortedBy { it.type.order }
        .toSet()
}
