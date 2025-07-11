package com.gravatar.app.usercomponent.domain.usecase

import com.gravatar.app.clock.AppClock
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.net.URI

@ExperimentalCoroutinesApi
class UploadAvatarUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var uploadAvatarUseCase: UploadAvatarUseCase
    private val userRepository = mockk<UserRepository>()
    private val avatarCacheBusterStorage = mockk<AvatarCacheBusterStorage>()
    private val clock = mockk<AppClock>()

    private val testAvatarFile = mockk<File>()
    private val testTimestamp = 1234567890L

    @Before
    fun setup() {
        every { clock.now() } returns testTimestamp
        coEvery { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) } returns Unit

        uploadAvatarUseCase = UploadAvatarUseCase(
            userRepository = userRepository,
            avatarCacheBusterStorage = avatarCacheBusterStorage,
            clock = clock
        )
    }

    @Test
    fun `invoke should call userRepository uploadAvatar with correct file`() = runTest {
        // Given
        coEvery { userRepository.uploadAvatar(any()) } returns GravatarResult.Success(createTestAvatar())

        // When
        uploadAvatarUseCase(testAvatarFile)

        // Then
        coVerify { userRepository.uploadAvatar(testAvatarFile) }
    }

    @Test
    fun `invoke should return success result when userRepository returns success`() = runTest {
        val testAvatar = createTestAvatar()
        // Given
        coEvery { userRepository.uploadAvatar(any()) } returns GravatarResult.Success(testAvatar)

        // When
        val result = uploadAvatarUseCase(testAvatarFile)

        // Then
        assert(result is GravatarResult.Success)
        assert((result as GravatarResult.Success).value == testAvatar)
    }

    @Test
    fun `invoke should return failure result when userRepository returns failure`() = runTest {
        // Given
        val errorType = ErrorType.Server
        coEvery { userRepository.uploadAvatar(any()) } returns GravatarResult.Failure(errorType)

        // When
        val result = uploadAvatarUseCase(testAvatarFile)

        // Then
        assert((result as GravatarResult.Failure).error == errorType)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
    }

    @Test
    fun `invoke should save avatar cache buster when upload is successful and avatar is selected`() = runTest {
        // Given
        val selectedAvatar = createTestAvatar(isSelected = true)
        coEvery { userRepository.uploadAvatar(any()) } returns GravatarResult.Success(selectedAvatar)

        // When
        val result = uploadAvatarUseCase(testAvatarFile)

        // Then
        assert(result is GravatarResult.Success)
        verify { clock.now() }
        coVerify { avatarCacheBusterStorage.saveAvatarCacheBuster(testTimestamp.toString()) }
    }

    @Test
    fun `invoke should not save avatar cache buster when upload is successful but avatar is not selected`() = runTest {
        // Given
        val nonSelectedAvatar = createTestAvatar() // Already has selected = false
        coEvery { userRepository.uploadAvatar(any()) } returns GravatarResult.Success(nonSelectedAvatar)

        // When
        val result = uploadAvatarUseCase(testAvatarFile)

        // Then
        assert(result is GravatarResult.Success)
        coVerify(exactly = 0) { avatarCacheBusterStorage.saveAvatarCacheBuster(any()) }
    }

    private fun createTestAvatar(isSelected: Boolean = false): Avatar = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test123")
        imageId = "test123"
        rating = Avatar.Rating.G
        altText = "Test Avatar"
        updatedDate = "2023-01-01"
        selected = isSelected
    }
}
