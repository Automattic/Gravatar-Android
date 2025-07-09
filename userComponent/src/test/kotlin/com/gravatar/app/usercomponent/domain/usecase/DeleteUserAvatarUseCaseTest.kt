package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.clock.AppClock
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
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

@ExperimentalCoroutinesApi
class DeleteUserAvatarUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var deleteUserAvatarUseCase: DeleteUserAvatarUseCase
    private val userRepository = mockk<UserRepository>()
    private val avatarCacheBusterStorage = mockk<AvatarCacheBusterStorage>()
    private val clock = mockk<AppClock>()

    private val testAvatarId = "test-avatar-id"
    private val testTimestamp = 1234567890L

    @Before
    fun setup() {
        coEvery { clock.now() } returns testTimestamp

        deleteUserAvatarUseCase = DeleteUserAvatarUseCase(
            userRepository = userRepository,
            avatarCacheBusterStorage = avatarCacheBusterStorage,
            clock = clock
        )
    }

    @Test
    fun `invoke should call userRepository deleteAvatar with correct avatarId`() = runTest {
        // Given
        coEvery { userRepository.deleteAvatar(any()) } returns Result.success(Unit)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) } returns Unit

        // When
        deleteUserAvatarUseCase(testAvatarId, isSelected = false)

        // Then
        coVerify { userRepository.deleteAvatar(testAvatarId) }
    }

    @Test
    fun `invoke should save avatar cache buster when isSelected is true and userRepository returns success`() = runTest {
        // Given
        coEvery { userRepository.deleteAvatar(any()) } returns Result.success(Unit)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(testTimestamp.toString()) } returns Unit

        // When
        val result = deleteUserAvatarUseCase(testAvatarId, isSelected = true)

        // Then
        assert(result.isSuccess)
        verify { clock.now() }
        coVerify { avatarCacheBusterStorage.saveAvatarCacheBuster(testTimestamp.toString()) }
    }

    @Test
    fun `invoke should not save avatar cache buster when isSelected is false and userRepository returns success`() = runTest {
        // Given
        coEvery { userRepository.deleteAvatar(any()) } returns Result.success(Unit)

        // When
        val result = deleteUserAvatarUseCase(testAvatarId, isSelected = false)

        // Then
        assert(result.isSuccess)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
    }

    @Test
    fun `invoke should not save avatar cache buster when userRepository returns failure`() = runTest {
        // Given
        val testException = RuntimeException("Test exception")
        coEvery { userRepository.deleteAvatar(any()) } returns Result.failure(testException)

        // When
        val result = deleteUserAvatarUseCase(testAvatarId, isSelected = true)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() == testException)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
    }
}
