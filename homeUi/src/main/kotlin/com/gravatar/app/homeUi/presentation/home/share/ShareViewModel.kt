package com.gravatar.app.homeUi.presentation.home.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.homeUi.presentation.DrawableUtils
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.usercomponent.domain.facade.PrivateContactInfoFacade
import com.gravatar.app.usercomponent.domain.facade.UserSharePreferencesFacade
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ShareViewModel(
    private val userRepository: UserRepository,
    private val getAvatarUrl: GetAvatarUrl,
    private val sharePreferencesFacade: UserSharePreferencesFacade,
    private val privateContactInfoFacade: PrivateContactInfoFacade,
    private val drawableUtils: DrawableUtils,
    private val fileUtils: FileUtils,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShareUiState())
    internal val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    private var saveContactInfoJob: Job? = null
    private val debounceDelay = 500L // 500ms debounce delay

    private val _actions = Channel<ShareAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        collectProfile()
        collectAvatarUrl()
        collectUserSharePreferences()
        collectPrivateContactInfo()
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
                savePrivateContactInfo()
            }

            is ShareEvent.OnPhoneValueChanged -> {
                _uiState.update {
                    it.copy(
                        privateContactInfo = it.privateContactInfo.copy(
                            privatePhone = shareEvent.value
                        )
                    )
                }
                savePrivateContactInfo()
            }

            is ShareEvent.OnAboutAppClicked -> showAboutAppDialog()
            is ShareEvent.OnDismissAboutAppDialog -> hideAboutAppDialog()
            is ShareEvent.OnUserSharePreferencesChanged -> handleUserSharePreferencesChange(shareEvent.shareFieldType)
            is ShareEvent.OnPrivateInformationClicked -> showPrivateInformationDialog()
            is ShareEvent.OnDismissPrivateInformationDialog -> hidePrivateInformationDialog()
            ShareEvent.OnShareClick -> shareVCard()
            ShareEvent.OnExpandQrCodeClick -> expandQrCode()
            ShareEvent.OnDismissExpandedQrCode -> hideExpandedQrCode()
        }
    }

    private fun expandQrCode() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isQrCodeExpanded = true)
            }

            _actions.send(ShareAction.ShowBottomBar(false))
        }
    }

    private fun hideExpandedQrCode() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isQrCodeExpanded = false)
            }

            _actions.send(ShareAction.ShowBottomBar(true))
        }
    }

    private fun shareVCard() {
        viewModelScope.launch {
            val vCardContent = uiState.value.vCardQrCodeData.exportToString(withPhoto = true)
            val vCardFile = fileUtils.createVCardFile(uiState.value.profile?.displayName.orEmpty(), vCardContent)

            _actions.send(ShareAction.ShareVCard(vCardFile))
        }
    }

    private fun handleUserSharePreferencesChange(shareFieldType: ShareFieldType) {
        with(_uiState.value.copyWithUserSharePreferences(shareFieldType)) {
            // update the UI state with the new preferences
            _uiState.value = this
            // Save the updated preferences
            viewModelScope.launch {
                sharePreferencesFacade.updatePreferences(this@with.userSharePreferences)
            }
        }
    }

    private fun savePrivateContactInfo() {
        // Cancel any existing job to avoid multiple saves
        saveContactInfoJob?.cancel()

        // Create a new job with debounce
        saveContactInfoJob = viewModelScope.launch {
            delay(debounceDelay) // Wait for the debounce period
            privateContactInfoFacade.updateContactInfo(_uiState.value.privateContactInfo)
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
                loadDrawable(avatarUrl?.toString())
                _uiState.update { currentState ->
                    currentState.copy(
                        avatarUrl = avatarUrl?.toString(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadDrawable(avatarUrl: String?) {
        viewModelScope.launch {
            avatarUrl?.let {
                val drawableAvatar = drawableUtils.downloadDrawable(avatarUrl)
                _uiState.update { currentState ->
                    currentState.copy(
                        avatarDrawable = drawableAvatar,
                    )
                }
            }
        }
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
        sharePreferencesFacade.getPreferences()
            .onEach { preferences ->
                _uiState.update { currentState ->
                    currentState.copy(
                        userSharePreferences = preferences
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun collectPrivateContactInfo() {
        privateContactInfoFacade.getContactInfo()
            .onEach { privateContactInfo ->
                _uiState.update { currentState ->
                    currentState.copy(
                        privateContactInfo = privateContactInfo
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
