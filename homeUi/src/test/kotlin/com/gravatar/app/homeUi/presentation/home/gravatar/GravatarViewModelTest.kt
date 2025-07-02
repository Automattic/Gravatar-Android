package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import androidx.core.net.toFile
import app.cash.turbine.test
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import io.mockk.coEvery
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
        coEvery { userRepository.uploadAvatar(file) } returns Result.success(newAvatar)

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
        } returns Result.failure(IllegalStateException("Test exception"))

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
                ),
                awaitItem()
            )
        }

        coVerify { userRepository.uploadAvatar(any()) }
        verify { fileUtils.deleteFile(mockUri) }
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
