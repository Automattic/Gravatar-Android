package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import androidx.core.net.toFile
import app.cash.turbine.test
import com.gravatar.AvatarUrl
import com.gravatar.app.homeUi.ImageDownloader
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserAvatar
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.app.usercomponent.domain.usecase.SelectUserAvatar
import com.gravatar.app.usercomponent.domain.usecase.UploadUserAvatar
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.types.Hash
import io.mockk.coEvery
import io.mockk.coJustAwait
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.net.URI
import java.net.URL

@Suppress("LargeClass")
@OptIn(ExperimentalCoroutinesApi::class)
class GravatarViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val getAvatarUrl: GetAvatarUrl = object : GetAvatarUrl {
        override fun invoke() = avatarUrlFlow
    }
    private val userRepository: UserRepository = mockk()
    private val selectUserAvatar: SelectUserAvatar = mockk()
    private val deleteUserAvatar: DeleteUserAvatar = mockk()
    private val uploadUserAvatar: UploadUserAvatar = mockk()
    private val fileUtils: FileUtils = mockk()
    private val imageDownloader: ImageDownloader = mockk()
    private lateinit var viewModel: GravatarViewModel

    private val avatarUrlFlow: MutableSharedFlow<URL?> = MutableSharedFlow()
    private val profileFlow: MutableSharedFlow<Profile?> = MutableSharedFlow()

    @Test
    fun `init should fetch avatars`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)

        // When
        initViewModel()

        // Then
        viewModel.uiState.test {
            assertEquals(GravatarUiState(), awaitItem())
            assertEquals(GravatarUiState(isLoading = true), awaitItem())
            assertEquals(GravatarUiState(isLoading = false, avatars = avatars), awaitItem())
        }
        coVerify { userRepository.getAvatars() }
    }

    @Test
    fun `onEvent Refresh with pullToRefresh true should fetch avatars with isRefreshing true`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()

        advanceUntilIdle()

        // When
        val refreshedAvatars = createAvatars(4)
        coEvery { userRepository.getAvatars() } returns Result.success(refreshedAvatars)
        viewModel.onEvent(GravatarEvent.Refresh(true))

        // Then
        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(GravatarUiState(isRefreshing = true, avatars = avatars), awaitItem())
            assertEquals(
                GravatarUiState(isRefreshing = false, avatars = refreshedAvatars),
                awaitItem()
            )
        }
        coVerify(exactly = 2) { userRepository.getAvatars() }
    }

    @Test
    fun `onEvent Refresh with pullToRefresh false should fetch avatars with isLoading true`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()

        advanceUntilIdle()

        // When
        val refreshedAvatars = createAvatars(4)
        coEvery { userRepository.getAvatars() } returns Result.success(refreshedAvatars)
        viewModel.onEvent(GravatarEvent.Refresh(false))

        // Then
        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(GravatarUiState(isLoading = true, avatars = avatars), awaitItem())
            assertEquals(
                GravatarUiState(isLoading = false, avatars = refreshedAvatars),
                awaitItem()
            )
        }
        coVerify(exactly = 2) { userRepository.getAvatars() }
    }

    @Test
    fun `onEvent Refresh with pullToRefresh true and null avatars should fetch avatars with isLoading true`() = runTest {
        // Given
        coEvery { userRepository.getAvatars() } returns Result.failure(IllegalStateException(""))
        initViewModel()

        advanceUntilIdle()

        // When
        val refreshedAvatars = createAvatars(4)
        coEvery { userRepository.getAvatars() } returns Result.success(refreshedAvatars)
        viewModel.onEvent(GravatarEvent.Refresh(true))

        // Then
        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(GravatarUiState(isLoading = true, isRefreshing = true, avatars = null), awaitItem())
            assertEquals(
                GravatarUiState(isLoading = false, isRefreshing = false, avatars = refreshedAvatars),
                awaitItem()
            )
        }
        coVerify(exactly = 2) { userRepository.getAvatars() }
    }

    @Test
    fun `fetchAvatars should handle failure`() = runTest {
        // Given
        coEvery { userRepository.getAvatars() } returns Result.failure(RuntimeException("Test exception"))

        // When
        initViewModel()

        // Then
        viewModel.uiState.test {
            assertEquals(GravatarUiState(), awaitItem())
            assertEquals(GravatarUiState(isLoading = true), awaitItem())
            assertEquals(GravatarUiState(isLoading = false), awaitItem())
        }
        coVerify { userRepository.getAvatars() }
    }

    @Test
    fun `onEvent OnAvatarSelected should select avatar successfully`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        coEvery { selectUserAvatar(avatarId) } returns Result.success(Unit)

        // When
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                selectedAvatarId = avatarId
            )
            assertEquals(expectedState, awaitItem())
        }
        coVerify { selectUserAvatar(avatarId) }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarSelected, awaitItem())
        }
    }

    @Test
    fun `onEvent OnAvatarSelected shouldn't select the same avatar again`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        coEvery { selectUserAvatar(avatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))
        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            // When
            viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))

            // Then
            expectNoEvents()
        }
        coVerify(exactly = 1) { selectUserAvatar(avatarId) }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarSelected, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `onEvent OnAvatarSelected should clean the loading state and skip selecting already selected avatar again`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val initialAvatarId = "1"
        val otherAvatarId = "2"
        coEvery { selectUserAvatar(initialAvatarId) } returns Result.success(Unit)
        coJustAwait { selectUserAvatar(otherAvatarId) }
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(initialAvatarId))
        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            // When
            viewModel.onEvent(GravatarEvent.OnAvatarSelected(otherAvatarId))
            assertEquals(
                GravatarUiState(
                    avatars = avatars,
                    selectedAvatarId = initialAvatarId,
                    selectingAvatarId = otherAvatarId
                ),
                awaitItem()
            )
            viewModel.onEvent(GravatarEvent.OnAvatarSelected(initialAvatarId))

            // Then
            assertEquals(
                GravatarUiState(
                    avatars = avatars,
                    selectedAvatarId = initialAvatarId,
                    selectingAvatarId = null
                ),
                awaitItem()
            )
        }
        coVerify(exactly = 1) { selectUserAvatar(initialAvatarId) }
    }

    @Test
    fun `onEvent OnAvatarSelected should handle failure`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        coEvery {
            selectUserAvatar(avatarId)
        } returns Result.failure(RuntimeException("Test exception"))

        // When
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars
            )
            assertEquals(expectedState, awaitItem())
        }
        coVerify { selectUserAvatar(avatarId) }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarSelectionFailed, awaitItem())
        }
    }

    @Test
    fun `onEvent OnLocalImageSelected should send LaunchImageCropper action`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        val mockUri = mockk<Uri>()
        val mockFile = mockk<File>()
        every { fileUtils.createCroppedAvatarFile() } returns mockFile

        initViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(GravatarEvent.OnLocalImageSelected(mockUri))
        advanceUntilIdle()

        // Then
        viewModel.actions.test {
            assertEquals(GravatarAction.LaunchImageCropper(mockUri, mockFile), awaitItem())
        }
        verify { fileUtils.createCroppedAvatarFile() }
    }

    @Test
    fun `onEvent OnImageCropped should upload avatar successfully`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val mockUri = mockk<Uri> {
            every { toFile() } returns file
        }
        every { fileUtils.deleteFile(mockUri) } returns Unit

        val newAvatar = createAvatar(4)
        coEvery { uploadUserAvatar(file) } returns GravatarResult.Success(newAvatar)

        // When
        viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))

        // Verify the state after setting uploadingAvatar
        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(
                GravatarUiState(
                    avatars = avatars,
                    uploadingAvatar = mockUri
                ),
                awaitItem()
            )
            assertEquals(
                GravatarUiState(
                    avatars = listOf(newAvatar) + avatars.filter { it.imageId != newAvatar.imageId },
                    uploadingAvatar = null
                ),
                awaitItem()
            )
        }

        coVerify { uploadUserAvatar(any()) }
        verify { fileUtils.deleteFile(mockUri) }
    }

    @Test
    fun `onEvent OnImageCropped should update selectedAvatarId and send AvatarSelected action when uploaded avatar is selected`() =
        runTest {
            // Given
            val avatars = createAvatars()
            coEvery { userRepository.getAvatars() } returns Result.success(avatars)
            initViewModel()
            advanceUntilIdle()

            mockkStatic("androidx.core.net.UriKt")
            val file = mockk<File>()
            val mockUri = mockk<Uri> {
                every { toFile() } returns file
            }
            every { fileUtils.deleteFile(mockUri) } returns Unit

            // Create a selected avatar
            val selectedAvatar = createAvatar(4, isSelected = true)
            coEvery { uploadUserAvatar(file) } returns GravatarResult.Success(selectedAvatar)

            // When
            viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))
            advanceUntilIdle()

            // Then
            // Verify the uiState is updated with the selectedAvatarId
            viewModel.uiState.test {
                val expectedState = GravatarUiState(
                    avatars = listOf(selectedAvatar) + avatars.filter { it.imageId != selectedAvatar.imageId },
                    uploadingAvatar = null,
                    selectedAvatarId = selectedAvatar.imageId
                )
                assertEquals(expectedState, awaitItem())
            }

            // Verify the AvatarSelected action is sent
            viewModel.actions.test {
                assertEquals(GravatarAction.AvatarSelected, awaitItem())
            }

            coVerify { uploadUserAvatar(any()) }
            verify { fileUtils.deleteFile(mockUri) }
        }

    @Test
    fun `onEvent OnImageCropped should handle failure`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val mockUri = mockk<Uri> {
            every { toFile() } returns file
        }
        every { fileUtils.deleteFile(mockUri) } returns Unit

        coEvery {
            uploadUserAvatar(any())
        } returns GravatarResult.Failure(ErrorType.Server)

        // When
        viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))

        // Verify the state after setting uploadingAvatar
        viewModel.uiState.test {
            expectMostRecentItem()
            assertEquals(
                GravatarUiState(
                    avatars = avatars,
                    uploadingAvatar = mockUri,
                ),
                awaitItem()
            )
            assertEquals(
                GravatarUiState(
                    avatars = avatars,
                    uploadingAvatar = null,
                    failedUploads = listOf(
                        AvatarUploadFailure(
                            uri = mockUri,
                            error = ErrorType.Server
                        )
                    )
                ),
                awaitItem()
            )
        }

        coVerify { uploadUserAvatar(any()) }
    }

    @Test
    fun `onEvent OnFailedAvatarDialogDismissed should clear failedUploadDialog`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        // Create a failed upload first
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val mockUri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery {
            uploadUserAvatar(any())
        } returns GravatarResult.Failure(ErrorType.Server)

        // Trigger a failed upload
        viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))
        advanceUntilIdle()

        // Show the failed upload dialog
        viewModel.onEvent(GravatarEvent.OnFailedAvatarTapped(mockUri))
        advanceUntilIdle()

        // When
        viewModel.onEvent(GravatarEvent.OnFailedAvatarDialogDismissed)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                failedUploads = listOf(
                    AvatarUploadFailure(
                        uri = mockUri,
                        error = ErrorType.Server
                    )
                ),
                failedUploadDialog = null
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `onEvent OnFailedAvatarDismissed should remove failed upload and clear dialog`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        // Create a failed upload first
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val mockUri = mockk<Uri> {
            every { toFile() } returns file
        }
        every { fileUtils.deleteFile(mockUri) } returns Unit
        coEvery {
            uploadUserAvatar(any())
        } returns GravatarResult.Failure(ErrorType.Server)

        // Trigger a failed upload
        viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))
        advanceUntilIdle()

        // Show the failed upload dialog
        viewModel.onEvent(GravatarEvent.OnFailedAvatarTapped(mockUri))
        advanceUntilIdle()

        // When
        viewModel.onEvent(GravatarEvent.OnFailedAvatarDismissed(mockUri))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                failedUploads = emptyList(),
                failedUploadDialog = null
            )
            assertEquals(expectedState, awaitItem())
        }
        verify { fileUtils.deleteFile(mockUri) }
    }

    @Test
    fun `onEvent OnFailedAvatarTapped should show failed upload dialog`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        // Create a failed upload first
        mockkStatic("androidx.core.net.UriKt")
        val file = mockk<File>()
        val mockUri = mockk<Uri> {
            every { toFile() } returns file
        }
        coEvery {
            uploadUserAvatar(any())
        } returns GravatarResult.Failure(ErrorType.Server)

        // Trigger a failed upload
        viewModel.onEvent(GravatarEvent.OnImageCropped(mockUri))
        advanceUntilIdle()

        // When
        viewModel.onEvent(GravatarEvent.OnFailedAvatarTapped(mockUri))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                failedUploads = listOf(
                    AvatarUploadFailure(
                        uri = mockUri,
                        error = ErrorType.Server
                    )
                ),
                failedUploadDialog = AvatarUploadFailure(
                    uri = mockUri,
                    error = ErrorType.Server
                )
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `onEvent OnDeleteAvatar should delete non-selected avatar successfully`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarIdToDelete = "2"
        coEvery { deleteUserAvatar(avatarIdToDelete, false) } returns Result.success(Unit)

        // When
        viewModel.onEvent(GravatarEvent.OnDeleteAvatar(avatarIdToDelete))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars.filter { it.imageId != avatarIdToDelete }
            )
            assertEquals(expectedState, awaitItem())
        }
        coVerify { deleteUserAvatar(avatarIdToDelete, false) }
    }

    @Test
    fun `onEvent OnDeleteAvatar should delete selected avatar successfully and update selectedAvatarId`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        // Select an avatar first
        val selectedAvatarId = "1"
        coEvery { selectUserAvatar(selectedAvatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(selectedAvatarId))
        advanceUntilIdle()

        // Show delete confirmation
        viewModel.onEvent(GravatarEvent.OnShowDeleteConfirmation(selectedAvatarId))
        advanceUntilIdle()

        // When
        coEvery { deleteUserAvatar(selectedAvatarId, true) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnDeleteAvatar(selectedAvatarId))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars.filter { it.imageId != selectedAvatarId },
                selectedAvatarId = null
            )
            assertEquals(expectedState, awaitItem())
        }
        coVerify { deleteUserAvatar(selectedAvatarId, true) }
    }

    @Test
    fun `onEvent OnDeleteAvatar should handle failure and restore avatar`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarIdToDelete = "2"
        coEvery { deleteUserAvatar(avatarIdToDelete, false) } returns Result.failure(RuntimeException("Test exception"))

        // When
        viewModel.onEvent(GravatarEvent.OnDeleteAvatar(avatarIdToDelete))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            // The state should be unchanged since the avatar should be restored after failure
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars
            )
            assertEquals(expectedState, awaitItem())
        }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarDeletionFailed, awaitItem())
        }
        coVerify { deleteUserAvatar(avatarIdToDelete, false) }
    }

    @Test
    fun `onEvent OnDeleteAvatar should handle failure and restore selected avatar`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        // Select an avatar first
        val selectedAvatarId = "1"
        coEvery { selectUserAvatar(selectedAvatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(selectedAvatarId))
        advanceUntilIdle()

        // When
        coEvery { deleteUserAvatar(selectedAvatarId, true) } returns Result.failure(RuntimeException("Test exception"))
        viewModel.onEvent(GravatarEvent.OnDeleteAvatar(selectedAvatarId))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            // The state should be unchanged since the avatar should be restored after failure
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                selectedAvatarId = selectedAvatarId
            )
            assertEquals(expectedState, awaitItem())
        }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarDeletionFailed, expectMostRecentItem())
        }
        coVerify { deleteUserAvatar(selectedAvatarId, true) }
    }

    @Test
    fun `onEvent OnShowDeleteConfirmation should update confirmAvatarDeletionId`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarIdToDelete = "2"

        // When
        viewModel.onEvent(GravatarEvent.OnShowDeleteConfirmation(avatarIdToDelete))
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                confirmAvatarDeletionId = avatarIdToDelete
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `onEvent OnDismissDeleteConfirmation should clear confirmAvatarDeletionId`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarIdToDelete = "2"
        viewModel.onEvent(GravatarEvent.OnShowDeleteConfirmation(avatarIdToDelete))
        advanceUntilIdle()

        // When
        viewModel.onEvent(GravatarEvent.OnDismissDeleteConfirmation)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = GravatarUiState(
                isLoading = false,
                avatars = avatars,
                confirmAvatarDeletionId = null
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `each avatar URL collected should be set as state`() = runTest {
        // Given
        coJustAwait { userRepository.getAvatars() }
        initViewModel()
        advanceUntilIdle()

        // When
        val avatarUrl = AvatarUrl(Hash("Hash")).url()
        avatarUrlFlow.emit(avatarUrl)

        // Then
        viewModel.uiState.test {
            assertEquals(GravatarUiState(isLoading = true, avatarUrl = avatarUrl.toString()), awaitItem())
        }
    }

    @Test
    fun `onEvent OnDownloadAvatar should download avatar image`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        val avatarUrl = avatars.first { it.imageId == avatarId }.imageUrl
        coEvery { imageDownloader.downloadImage(avatarUrl) } returns GravatarResult.Success(Unit)

        // When
        viewModel.onEvent(GravatarEvent.OnDownloadAvatar(avatarId))
        advanceUntilIdle()

        // Then
        coVerify { imageDownloader.downloadImage(avatarUrl) }
        viewModel.actions.test {
            assertEquals(GravatarAction.AvatarDownloadStarted, awaitItem())
        }
    }

    @Test
    fun `onEvent OnDownloadAvatar should handle download manager not available`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        val avatarUrl = avatars.first { it.imageId == avatarId }.imageUrl
        coEvery {
            imageDownloader.downloadImage(avatarUrl)
        } returns GravatarResult.Failure(com.gravatar.app.homeUi.DownloadManagerError.DOWNLOAD_MANAGER_NOT_AVAILABLE)

        // When
        viewModel.onEvent(GravatarEvent.OnDownloadAvatar(avatarId))
        advanceUntilIdle()

        // Then
        coVerify { imageDownloader.downloadImage(avatarUrl) }
        viewModel.actions.test {
            assertEquals(GravatarAction.DownloadManagerNotAvailable, awaitItem())
        }
    }

    @Test
    fun `onEvent OnDownloadAvatar should handle download manager disabled`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        val avatarUrl = avatars.first { it.imageId == avatarId }.imageUrl
        coEvery {
            imageDownloader.downloadImage(avatarUrl)
        } returns GravatarResult.Failure(com.gravatar.app.homeUi.DownloadManagerError.DOWNLOAD_MANAGER_DISABLED)

        // When
        viewModel.onEvent(GravatarEvent.OnDownloadAvatar(avatarId))
        advanceUntilIdle()

        // Then
        coVerify { imageDownloader.downloadImage(avatarUrl) }
        // No action should be emitted
        viewModel.actions.test {
            expectNoEvents()
        }
    }

    private fun initViewModel() {
        every { userRepository.getProfile() } returns profileFlow

        viewModel = GravatarViewModel(
            getAvatarUrl = getAvatarUrl,
            selectUserAvatar = selectUserAvatar,
            deleteUserAvatar = deleteUserAvatar,
            uploadUserAvatar = uploadUserAvatar,
            userRepository = userRepository,
            fileUtils = fileUtils,
            imageDownloader = imageDownloader,
        )
    }

    private fun createAvatars(count: Int = 3): List<Avatar> {
        return List(count) { index ->
            createAvatar(index)
        }
    }

    private fun createAvatar(id: Int, isSelected: Boolean = false): Avatar = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test$id")
        imageId = id.toString()
        rating = Avatar.Rating.G
        altText = "alt$id"
        updatedDate = ""
        selected = isSelected
    }
}
