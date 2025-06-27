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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ProfileViewModel(private val profileService: ProfileService) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    internal val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

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
}

internal fun Profile.aboutFields(): Set<AboutEditorField> {
    return AboutInputField.entries
        .map {
            AboutEditorField(
                type = it,
                value = when (it) {
                    AboutInputField.DISPLAY_NAME -> displayName
                    AboutInputField.ABOUT_ME -> description
                    AboutInputField.PRONOUNS -> pronouns
                    AboutInputField.PRONUNCIATION -> pronunciation
                    AboutInputField.LOCATION -> location
                    AboutInputField.JOB_TITLE -> jobTitle
                    AboutInputField.COMPANY -> company
                    AboutInputField.FIRST_NAME -> firstName.orEmpty()
                    AboutInputField.LAST_NAME -> lastName.orEmpty()
                    AboutInputField.CELL_PHONE -> contactInfo?.cellPhone.orEmpty()
                    AboutInputField.CONTACT_EMAIL -> contactInfo?.email.orEmpty()
                },
                maxLines = when (it) {
                    AboutInputField.ABOUT_ME -> 4
                    else -> 1
                },
            )
        }
        .sortedBy { it.type.order }
        .toSet()
}
