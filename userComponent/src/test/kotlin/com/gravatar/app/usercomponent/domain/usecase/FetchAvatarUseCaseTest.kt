package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.clock.AppClock
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

@ExperimentalCoroutinesApi
class FetchAvatarUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var fetchAvatarsUseCase: FetchAvatarsUseCase
    private val userRepository = mockk<UserRepository>()
    private val avatarCacheBusterStorage = mockk<AvatarCacheBusterStorage>()
    private val clock = mockk<AppClock>()

    private val testTimestamp = 1234567890L

    @Before
    fun setup() {
        coEvery { clock.now() } returns testTimestamp

        fetchAvatarsUseCase = FetchAvatarsUseCase(
            userRepository = userRepository,
            avatarCacheBusterStorage = avatarCacheBusterStorage,
            clock = clock
        )
    }

    @Test
    fun `invoke should call userRepository getAvatars`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) } returns Unit

        // When
        fetchAvatarsUseCase()

        // Then
        coVerify { userRepository.getAvatars() }
    }

    @Test
    fun `invoke should save avatar cache buster and userRepository returns success`() = runTest {
        // Given
        val avatars = createAvatars()
        coEvery { userRepository.getAvatars() } returns Result.success(avatars)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) } returns Unit

        // When
        val result = fetchAvatarsUseCase()

        // Then
        assert(result.isSuccess)
        verify { clock.now() }
        coVerify { avatarCacheBusterStorage.saveAvatarCacheBuster(testTimestamp.toString()) }
    }

    @Test
    fun `invoke should not save avatar cache buster when userRepository returns failure`() = runTest {
        // Given
        val testException = RuntimeException("Test exception")
        coEvery { userRepository.getAvatars() } returns Result.failure(testException)

        // When
        val result = fetchAvatarsUseCase()

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() == testException)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
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
