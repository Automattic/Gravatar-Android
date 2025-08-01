package com.gravatar.app.homeUi.presentation.home.components.topbar.components.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AboutAppDialogViewModel(
    private val deleteUserProfile: DeleteUserProfile,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutAppDialogState())
    val uiState: StateFlow<AboutAppDialogState> = _uiState.asStateFlow()

    fun onEvent(event: AboutAppDialogEvent) {
        when (event) {
            AboutAppDialogEvent.OnShowDeleteConfirmation -> {
                showDeleteConfirmation()
            }

            AboutAppDialogEvent.OnHideDeleteConfirmation -> {
                hideDeleteConfirmation()
            }

            AboutAppDialogEvent.OnConfirmDeleteAccount -> {
                deleteProfile()
                hideDeleteConfirmation()
            }
        }
    }

    private fun showDeleteConfirmation() {
        _uiState.update { currentState ->
            currentState.copy(isDeleteConfirmationVisible = true)
        }
    }

    private fun hideDeleteConfirmation() {
        _uiState.update { currentState ->
            currentState.copy(isDeleteConfirmationVisible = false)
        }
    }

    private fun deleteProfile() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true)
            }
            deleteUserProfile().onFailure {
                _uiState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
        }
    }
}
