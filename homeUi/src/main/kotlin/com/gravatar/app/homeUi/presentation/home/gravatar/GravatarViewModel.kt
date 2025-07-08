package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.app.usercomponent.domain.usecase.SelectUserAvatar
import com.gravatar.services.GravatarResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class GravatarViewModel(
    private val getAvatarUrl: GetAvatarUrl,
    private val selectUserAvatar: SelectUserAvatar,
    private val userRepository: UserRepository,
    private val fileUtils: FileUtils,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GravatarUiState())
    val uiState: StateFlow<GravatarUiState> = _uiState.asStateFlow()

    private val _actions = Channel<GravatarAction>(Channel.BUFFERED)
    val actions = _actions.receiveAsFlow()

    private val avatarSelectionQueue = Channel<String>(Channel.CONFLATED)

    init {
        fetchAvatars(isRefreshing = false)

        viewModelScope.launch {
            avatarSelectionQueue.receiveAsFlow()
                .collectLatest { avatarId ->
                    selectAvatar(avatarId)
                }
        }
        collectUserAvatar()
    }

    fun onEvent(event: GravatarEvent) {
        when (event) {
            GravatarEvent.Refresh -> fetchAvatars(isRefreshing = true)
            is GravatarEvent.OnAvatarSelected -> addAvatarSelectionTOQueue(event.avatarId)
            is GravatarEvent.OnLocalImageSelected -> localImageSelected(event.uri)
            is GravatarEvent.OnImageCropped -> uploadAvatar(event.uri)
            GravatarEvent.OnFailedAvatarDialogDismissed -> dismissFailedUploadDialog()
            is GravatarEvent.OnFailedAvatarDismissed -> removedFailedUpload(event.uri)
            is GravatarEvent.OnFailedAvatarTapped -> showFailedUploadDialog(event.uri)
            is GravatarEvent.OnDeleteAvatar -> deleteAvatar(event.avatarId)
            is GravatarEvent.OnShowDeleteConfirmation -> showDeleteConfirmation(event.avatarId)
            GravatarEvent.OnDismissDeleteConfirmation -> dismissDeleteConfirmation()
        }
    }

    private fun addAvatarSelectionTOQueue(avatarId: String) {
        val state = uiState.value
        if (state.selectedAvatarId != avatarId || state.selectingAvatarId != avatarId) {
            viewModelScope.launch {
                avatarSelectionQueue.send(avatarId)
            }
        }
    }

    private fun showFailedUploadDialog(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(failedUploadDialog = currentState.failedUploads.firstOrNull { it.uri == uri })
        }
    }

    private fun dismissFailedUploadDialog() {
        _uiState.update { currentState ->
            currentState.copy(failedUploadDialog = null)
        }
    }

    private fun showDeleteConfirmation(avatarId: String) {
        _uiState.update { currentState ->
            currentState.copy(confirmAvatarDeletionId = avatarId)
        }
    }

    private fun dismissDeleteConfirmation() {
        _uiState.update { currentState ->
            currentState.copy(confirmAvatarDeletionId = null)
        }
    }

    private fun removedFailedUpload(uri: Uri) {
        fileUtils.deleteFile(uri)
        _uiState.update { currentState ->
            currentState.copy(
                failedUploads = currentState.failedUploads.filter { it.uri != uri },
                failedUploadDialog = null,
            )
        }
    }

    private fun uploadAvatar(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    uploadingAvatar = uri,
                    failedUploads = currentState.failedUploads.filter { it.uri != uri },
                    failedUploadDialog = null,
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

    private suspend fun selectAvatar(avatarId: String) {
        if (uiState.value.selectedAvatarId == avatarId) {
            _uiState.update { currentState ->
                currentState.copy(selectingAvatarId = null)
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(selectingAvatarId = avatarId)
            }
            selectUserAvatar(avatarId)
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

    private fun deleteAvatar(avatarId: String) {
        viewModelScope.launch {
            val avatarIndex = _uiState.value.avatars.indexOfFirstOrNull { it.imageId == avatarId }
            val isSelectedAvatar = avatarId == _uiState.value.selectedAvatarId
            val avatar = avatarIndex?.let { _uiState.value.avatars[avatarIndex] }
            if (avatar != null) {
                _uiState.update { currentState ->
                    val updatedAvatars = currentState.avatars.toMutableList().filter { it.imageId != avatarId }
                    currentState.copy(
                        avatars = updatedAvatars,
                        selectedAvatarId = if (isSelectedAvatar) {
                            null
                        } else {
                            currentState.selectedAvatarId
                        },
                        confirmAvatarDeletionId = null,
                    )
                }
                userRepository.deleteAvatar(avatarId)
                    .onSuccess { result ->
                        // NOTIFY THE UI TO SHOW THE CONFIRMATION
                    }
                    .onFailure { error ->
                        // NOTIFY THE UI TO SHOW AN ERROR

                        _uiState.update { currentState ->
                            val updatedAvatars = currentState.avatars.toMutableList().apply {
                                add(avatarIndex, avatar)
                            }
                            currentState.copy(
                                avatars = updatedAvatars,
                                selectedAvatarId = if (isSelectedAvatar) {
                                    avatarId
                                } else {
                                    currentState.selectedAvatarId
                                },
                            )
                        }
                    }
            }
        }
    }

    private fun collectUserAvatar() {
        getAvatarUrl()
            .onEach { url ->
                _uiState.update { currentState ->
                    currentState.copy(avatarUrl = url.toString())
                }
            }
            .launchIn(viewModelScope)
    }
}

private inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst { predicate(it) }
    return if (index == -1) null else index
}
