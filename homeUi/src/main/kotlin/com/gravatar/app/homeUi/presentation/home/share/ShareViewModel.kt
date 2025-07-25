package com.gravatar.app.homeUi.presentation.home.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.app.usercomponent.domain.usecase.GetUserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.UpdateUserSharePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ShareViewModel(
    private val userRepository: UserRepository,
    private val getAvatarUrl: GetAvatarUrl,
    private val getUserSharePreferences: GetUserSharePreferences,
    private val updateUserSharePreferences: UpdateUserSharePreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    internal val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    init {
        collectProfile()
        collectAvatarUrl()
        collectUserSharePreferences()
    }

    fun onEvent(shareEvent: ShareEvent) {
        when (shareEvent) {
            is ShareEvent.OnEmailValueChanged -> {
                _uiState.update {
                    it.copy(
                        privateContactInfo = it.privateContactInfo.copy(
                            privateEmail = shareEvent.value
                        )
                    )
                }
            }

            is ShareEvent.OnPhoneValueChanged -> {
                _uiState.update {
                    it.copy(
                        privateContactInfo = it.privateContactInfo.copy(
                            privatePhone = shareEvent.value
                        )
                    )
                }
            }

            is ShareEvent.OnAboutAppClicked -> showAboutAppDialog()
            is ShareEvent.OnDismissAboutAppDialog -> hideAboutAppDialog()
            is ShareEvent.OnUserSharePreferencesChanged -> handleUserSharePreferencesChange(shareEvent.shareFieldType)
            is ShareEvent.OnPrivateInformationClicked -> showPrivateInformationDialog()
            is ShareEvent.OnDismissPrivateInformationDialog -> hidePrivateInformationDialog()
        }
    }

    private fun handleUserSharePreferencesChange(shareFieldType: ShareFieldType) {
        with(_uiState.value.copyWithUserSharePreferences(shareFieldType)) {
            // update the UI state with the new preferences
            _uiState.value = this
            // Save the updated preferences
            viewModelScope.launch {
                updateUserSharePreferences(this@with.userSharePreferences)
            }
        }
    }

    private fun showAboutAppDialog() {
        _uiState.update { currentState ->
            currentState.copy(isAboutAppDialogVisible = true)
        }
    }

    private fun hideAboutAppDialog() {
        _uiState.update { currentState ->
            currentState.copy(isAboutAppDialogVisible = false)
        }
    }

    private fun showPrivateInformationDialog() {
        _uiState.update { currentState ->
            currentState.copy(isPrivateInformationDialogVisible = true)
        }
    }

    private fun hidePrivateInformationDialog() {
        _uiState.update { currentState ->
            currentState.copy(isPrivateInformationDialogVisible = false)
        }
    }

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

    private fun collectUserSharePreferences() {
        getUserSharePreferences()
            .onEach { preferences ->
                _uiState.update { currentState ->
                    currentState.copy(
                        userSharePreferences = preferences
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
