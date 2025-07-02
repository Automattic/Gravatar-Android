package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.services.GravatarResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class GravatarViewModel(
    val userRepository: UserRepository,
    val fileUtils: FileUtils,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GravatarUiState())
    val uiState: StateFlow<GravatarUiState> = _uiState.asStateFlow()

    private val _actions = Channel<GravatarAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    init {
        fetchAvatars(isRefreshing = false)
    }

    fun onEvent(event: GravatarEvent) {
        when (event) {
            GravatarEvent.Refresh -> fetchAvatars(isRefreshing = true)
            is GravatarEvent.OnAvatarSelected -> selectAvatar(event.avatarId)
            is GravatarEvent.OnLocalImageSelected -> localImageSelected(event.uri)
            is GravatarEvent.OnImageCropped -> uploadAvatar(event.uri)
        }
    }

    private fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    uploadingAvatar = uri,
                    failedUploads = currentState.failedUploads.filter { it.uri != uri },
                )
            }
            when (val result = userRepository.uploadAvatar(uri.toFile())) {
                is GravatarResult.Success -> {
                    val avatar = result.value
                    fileUtils.deleteFile(uri)
                    _uiState.update { currentState ->
                        currentState.copy(
                            avatars = buildList {
                                add(avatar)
                                addAll(
                                    currentState.avatars
                                        .filter { it.imageId != avatar.imageId }
                                )
                            },
                            uploadingAvatar = null,
                        )
                    }
                }

                is GravatarResult.Failure -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            uploadingAvatar = null,
                            failedUploads = currentState.failedUploads + AvatarUploadFailure(
                                uri,
                                error = result.error
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun localImageSelected(imageUri: Uri) {
        viewModelScope.launch {
            _actions.send(
                GravatarAction.LaunchImageCropper(
                    imageUri,
                    fileUtils.createCroppedAvatarFile()
                )
            )
        }
    }

    private fun selectAvatar(avatarId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectingAvatarId = avatarId)
            }
            userRepository.selectAvatar(avatarId)
                .onSuccess {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectingAvatarId = null,
                            selectedAvatarId = avatarId,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectingAvatarId = null,
                        )
                    }
                }
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
                        currentState.copy(
                            avatars = avatars,
                            selectedAvatarId = avatars.firstOrNull { it.selected == true }?.imageId,
                            isRefreshing = false,
                            isLoading = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isRefreshing = false,
                            isLoading = false,
                        )
                    }
                }
        }
    }
}
