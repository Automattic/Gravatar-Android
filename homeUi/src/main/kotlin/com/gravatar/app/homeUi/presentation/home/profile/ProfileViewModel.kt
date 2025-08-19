package com.gravatar.app.homeUi.presentation.home.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ProfileViewModel(
    private val getAvatarUrl: GetAvatarUrl,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    internal val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _actions = Channel<ProfileAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        refreshProfile()
        collectProfile()
        collectAvatarUrl()
    }

    fun onEvent(profileEvent: ProfileEvent) {
        when (profileEvent) {
            is ProfileEvent.OnProfileFieldUpdated -> updateProfileField(profileEvent.aboutField)
            ProfileEvent.OnSaveClicked -> saveChanges()
            ProfileEvent.OnCancelClicked -> cancelChanges()
            ProfileEvent.OnRefreshProfile -> refreshProfile(pullToRefresh = true)
            ProfileEvent.OnProfileLinkClicked -> openProfileUrl()
            ProfileEvent.OnAboutAppClicked -> showAboutAppDialog()
            ProfileEvent.OnDismissAboutAppDialog -> dismissAboutAppDialog()
            ProfileEvent.OnPrivacySettingClicked -> showPrivacySettings()
            ProfileEvent.OnPrivacySettingDismissed -> dismissPrivacySettings()
        }
    }

    private fun cancelChanges() {
        _uiState.update { currentState ->
            currentState.copy(editedAboutFields = emptyMap())
        }
    }

    private fun openProfileUrl() {
        viewModelScope.launch {
            val profile = _uiState.value.profile
            profile?.profileUrl?.toString()?.let { url ->
                _actions.send(ProfileAction.OpenProfileUrl(url))
            }
        }
    }

    private fun refreshProfile(pullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true, isRefreshing = pullToRefresh)
            }
            userRepository.refreshProfile()
                .onSuccess {
                    _uiState.update { currentState ->
                        currentState.copy(isRefreshing = false, isLoading = false)
                    }
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(isRefreshing = false, isLoading = false)
                    }
                }
        }
    }

    private fun updateProfileField(aboutField: AboutEditorField) {
        _uiState.update { currentState ->
            val updatedEditedFields = currentState.editedAboutFields.toMutableMap()

            val originalField = currentState.originalAboutFields.find { it.type == aboutField.type }

            if (originalField != null && originalField.value == aboutField.value) {
                // If the value is the same as the original, remove it from edited fields
                updatedEditedFields.remove(aboutField.type)
            } else {
                // Otherwise, store the edited value
                updatedEditedFields[aboutField.type] = aboutField.value
            }

            currentState.copy(editedAboutFields = updatedEditedFields)
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val editedFields = currentState.editedAboutFields

            if (editedFields.isEmpty()) return@launch

            _uiState.update { it.copy(isSavingProfile = true) }

            userRepository.updateProfile(editedFields.updateProfileRequest())
                .onSuccess { updatedProfile ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isSavingProfile = false,
                            editedAboutFields = emptyMap()
                        )
                    }
                    _actions.send(ProfileAction.ProfileSaved)
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(isSavingProfile = false)
                    }
                    _actions.send(ProfileAction.ProfileSaveFailed)
                }
        }
    }

    private fun collectAvatarUrl() {
        getAvatarUrl()
            .onEach { url ->
                _uiState.update { currentState ->
                    currentState.copy(avatarUrl = url?.toString())
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

    private fun showAboutAppDialog() {
        _uiState.update { currentState ->
            currentState.copy(isAboutAppDialogVisible = true)
        }
    }

    private fun dismissAboutAppDialog() {
        _uiState.update { currentState ->
            currentState.copy(isAboutAppDialogVisible = false)
        }
    }

    private fun showPrivacySettings() {
        _uiState.update { currentState ->
            currentState.copy(isPrivacySettingsVisible = true)
        }
    }

    private fun dismissPrivacySettings() {
        _uiState.update { currentState ->
            currentState.copy(isPrivacySettingsVisible = false)
        }
    }
}

internal fun Map<AboutInputField, String>.updateProfileRequest() =
    UpdateProfileRequest {
        forEach { (field, value) ->
            when (field) {
                AboutInputField.DISPLAY_NAME -> displayName = value
                AboutInputField.ABOUT_ME -> description = value
                AboutInputField.PRONOUNS -> pronouns = value
                AboutInputField.PRONUNCIATION -> pronunciation = value
                AboutInputField.LOCATION -> location = value
                AboutInputField.JOB_TITLE -> jobTitle = value
                AboutInputField.COMPANY -> company = value
                AboutInputField.FIRST_NAME -> firstName = value
                AboutInputField.LAST_NAME -> lastName = value
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
                },
                maxLines = when (it) {
                    AboutInputField.ABOUT_ME -> 4
                    else -> 1
                },
                edited = false
            )
        }
        .sortedBy { it.type.order }
        .toSet()
}
