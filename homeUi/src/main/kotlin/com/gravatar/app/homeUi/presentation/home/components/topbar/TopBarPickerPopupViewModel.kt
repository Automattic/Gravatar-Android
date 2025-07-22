package com.gravatar.app.homeUi.presentation.home.components.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.restapi.models.Profile
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class TopBarPickerPopupViewModel(
    private val userRepository: UserRepository,
    private val logout: Logout,
) : ViewModel() {

    private val _actions = Channel<TopBarPickerPopupAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    private var profile: Profile? = null

    init {
        collectUserProfile()
    }

    fun onEvent(event: TopBarPickerPopupEvent) {
        when (event) {
            TopBarPickerPopupEvent.OnLogoutSelected -> logoutUser()
            TopBarPickerPopupEvent.OnProfileLinkClicked -> openProfileUrl()
            TopBarPickerPopupEvent.OnGravatarLinkClicked -> openGravatarWebsite()
            TopBarPickerPopupEvent.OnShareProfileClicked -> shareProfileUrl()
        }
    }

    private fun openUrl(url: String) {
        viewModelScope.launch {
            _actions.send(TopBarPickerPopupAction.OpenExternalUrl(url))
        }
    }

    private fun openProfileUrl() {
        getProfileUrl()?.let { url ->
            openUrl(url)
        }
    }

    private fun shareProfileUrl() {
        viewModelScope.launch {
            getProfileUrl()?.let { url ->
                _actions.send(TopBarPickerPopupAction.ShareProfileUrl(url))
            }
        }
    }

    private fun getProfileUrl(): String? = profile?.profileUrl?.toString()

    private fun openGravatarWebsite() {
        openUrl("https://www.gravatar.com")
    }

    private fun logoutUser() {
        viewModelScope.launch {
            logout()
        }
    }

    private fun collectUserProfile() {
        userRepository.getProfile()
            .onEach { newProfile ->
                profile = newProfile
            }
            .launchIn(viewModelScope)
    }
}
