package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SelectAvatarUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var selectAvatarUseCase: SelectAvatarUseCase
    private val userRepository = mockk<UserRepository>()
    private val avatarCacheBusterStorage = mockk<AvatarCacheBusterStorage>()

    private val testAvatarId = "test-avatar-id"

    @Before
    fun setup() {
        selectAvatarUseCase = SelectAvatarUseCase(
            userRepository = userRepository,
            avatarCacheBusterStorage = avatarCacheBusterStorage,
        )
    }

    @Test
    fun `invoke should call userRepository selectAvatar with correct avatarId`() = runTest {
        // Given
        coEvery { userRepository.selectAvatar(any()) } returns Result.success(Unit)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(testAvatarId) } returns Unit

        // When
        selectAvatarUseCase(testAvatarId)

        // Then
        coVerify { userRepository.selectAvatar(testAvatarId) }
    }

    @Test
    fun `invoke should save avatar cache buster when userRepository returns success`() = runTest {
        // Given
        coEvery { userRepository.selectAvatar(any()) } returns Result.success(Unit)
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(testAvatarId) } returns Unit

        // When
        val result = selectAvatarUseCase(testAvatarId)

        // Then
        assert(result.isSuccess)
        coVerify { avatarCacheBusterStorage.saveAvatarCacheBuster(testAvatarId) }
    }

    @Test
    fun `invoke should not save avatar cache buster when userRepository returns failure`() = runTest {
        // Given
        val testException = RuntimeException("Test exception")
        coEvery { userRepository.selectAvatar(any()) } returns Result.failure(testException)

        // When
        val result = selectAvatarUseCase(testAvatarId)

        // Then
        assert(result.isFailure)
        assert(result.exceptionOrNull() == testException)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
    }
}
