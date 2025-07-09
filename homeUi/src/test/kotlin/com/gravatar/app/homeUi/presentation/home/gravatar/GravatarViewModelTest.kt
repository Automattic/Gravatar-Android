package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import androidx.core.net.toFile
import app.cash.turbine.test
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import io.mockk.coEvery
import io.mockk.coJustAwait
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class GravatarViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val userRepository: UserRepository = mockk()
    private val fileUtils: FileUtils = mockk()
    private lateinit var viewModel: GravatarViewModel

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
    fun `onEvent Refresh should fetch avatars with isRefreshing true`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()

        advanceUntilIdle()

        // When
        val refreshedAvatars = createAvatars(4)
        coEvery { userRepository.getAvatars() } returns Result.success(refreshedAvatars)
        viewModel.onEvent(GravatarEvent.Refresh)

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
        coEvery { userRepository.selectAvatar(avatarId) } returns Result.success(Unit)

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
        coVerify { userRepository.selectAvatar(avatarId) }
    }

    @Test
    fun `onEvent OnAvatarSelected shouldn't select the same avatar again`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarId = "1"
        coEvery { userRepository.selectAvatar(avatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))
        advanceUntilIdle()

        viewModel.uiState.test {
            expectMostRecentItem()

            // When
            viewModel.onEvent(GravatarEvent.OnAvatarSelected(avatarId))

            // Then
            expectNoEvents()
        }
        coVerify(exactly = 1) { userRepository.selectAvatar(avatarId) }
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
        coEvery { userRepository.selectAvatar(initialAvatarId) } returns Result.success(Unit)
        coJustAwait { userRepository.selectAvatar(otherAvatarId) }
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
        coVerify(exactly = 1) { userRepository.selectAvatar(initialAvatarId) }
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
            userRepository.selectAvatar(avatarId)
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
        coVerify { userRepository.selectAvatar(avatarId) }
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
        coEvery { userRepository.uploadAvatar(file) } returns GravatarResult.Success(newAvatar)

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

        coVerify { userRepository.uploadAvatar(any()) }
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
            userRepository.uploadAvatar(any())
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

        coVerify { userRepository.uploadAvatar(any()) }
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
            userRepository.uploadAvatar(any())
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
            userRepository.uploadAvatar(any())
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
            userRepository.uploadAvatar(any())
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
        coEvery { userRepository.deleteAvatar(avatarIdToDelete) } returns Result.success(Unit)

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
        coVerify { userRepository.deleteAvatar(avatarIdToDelete) }
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
        coEvery { userRepository.selectAvatar(selectedAvatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(selectedAvatarId))
        advanceUntilIdle()

        // Show delete confirmation
        viewModel.onEvent(GravatarEvent.OnShowDeleteConfirmation(selectedAvatarId))
        advanceUntilIdle()

        // When
        coEvery { userRepository.deleteAvatar(selectedAvatarId) } returns Result.success(Unit)
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
        coVerify { userRepository.deleteAvatar(selectedAvatarId) }
    }

    @Test
    fun `onEvent OnDeleteAvatar should handle failure and restore avatar`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        initViewModel()
        advanceUntilIdle()

        val avatarIdToDelete = "2"
        coEvery { userRepository.deleteAvatar(avatarIdToDelete) } returns Result.failure(RuntimeException("Test exception"))

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
        coVerify { userRepository.deleteAvatar(avatarIdToDelete) }
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
        coEvery { userRepository.selectAvatar(selectedAvatarId) } returns Result.success(Unit)
        viewModel.onEvent(GravatarEvent.OnAvatarSelected(selectedAvatarId))
        advanceUntilIdle()

        // When
        coEvery { userRepository.deleteAvatar(selectedAvatarId) } returns Result.failure(RuntimeException("Test exception"))
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
        coVerify { userRepository.deleteAvatar(selectedAvatarId) }
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

    private fun initViewModel() {
        viewModel = GravatarViewModel(
            userRepository = userRepository,
            fileUtils = fileUtils,
        )
    }

    private fun createAvatars(count: Int = 3): List<Avatar> {
        return List(count) { index ->
            createAvatar(index)
        }
    }

    private fun createAvatar(id: Int): Avatar = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test$id")
        imageId = id.toString()
        rating = Avatar.Rating.G
        altText = "alt$id"
        updatedDate = ""
    }
}
