package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfile
import kotlinx.coroutines.launch

class AboutAppDialogViewModel(
    private val deleteUserProfile: DeleteUserProfile,
) : ViewModel() {

    fun onEvent(event: AboutAppDialogEvent) {
        when (event) {
            AboutAppDialogEvent.OnDeleteAccount -> {
                deleteProfile()
            }
        }
    }

    private fun deleteProfile() {
        viewModelScope.launch {
            deleteUserProfile()
        }
    }
}
