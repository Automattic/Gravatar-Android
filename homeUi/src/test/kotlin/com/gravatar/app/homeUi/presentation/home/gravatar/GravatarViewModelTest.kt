package com.gravatar.app.homeUi.presentation.home.gravatar

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class GravatarViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val userRepository: UserRepository = mockk()
    private lateinit var viewModel: GravatarViewModel

    @Test
    fun `init should fetch avatars`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)

        // When
        viewModel = GravatarViewModel(userRepository)

        // Then
        viewModel.uiState.test {
            assertEquals(GravatarUiState(), awaitItem())
            assertEquals(GravatarUiState(isLoading = true), awaitItem())
            assertEquals(GravatarUiState(isLoading = true, avatars = avatars), awaitItem())
            assertEquals(GravatarUiState(isLoading = false, avatars = avatars), awaitItem())
        }
        coVerify { userRepository.getAvatars() }
    }

    @Test
    fun `onEvent Refresh should fetch avatars with isRefreshing true`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        viewModel = GravatarViewModel(userRepository)

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
                GravatarUiState(isRefreshing = true, avatars = refreshedAvatars),
                awaitItem()
            )
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
        viewModel = GravatarViewModel(userRepository)

        // Then
        viewModel.uiState.test {
            assertEquals(GravatarUiState(), awaitItem())
            assertEquals(GravatarUiState(isLoading = true), awaitItem())
            assertEquals(GravatarUiState(isLoading = false), awaitItem())
        }
        coVerify { userRepository.getAvatars() }
    }

    private fun createAvatars(count: Int = 3): List<Avatar> {
        return List(count) { index ->
            Avatar {
                imageUrl = URI.create("https://gravatar.com/avatar/test$index")
                imageId = index.toString()
                rating = Avatar.Rating.G
                altText = "alt$index"
                updatedDate = ""
            }
        }
    }
}
