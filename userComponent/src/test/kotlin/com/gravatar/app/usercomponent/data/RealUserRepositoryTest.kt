package com.gravatar.app.usercomponent.data

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.types.Hash
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class RealUserRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var repository: RealUserRepository
    private val avatarService = mockk<AvatarService>()
    private val profileRepository = mockk<ProfileRepository>()
    private val tokenStorage = FakeAuthTokenStorage()

    private val testToken = "test-token"
    private val testHash = "test-hash"

    @Before
    fun setup() {
        repository = RealUserRepository(
            avatarService = avatarService,
            profileRepository = profileRepository,
            tokenStorage = tokenStorage
        )
    }

    @Test
    fun `getAvatars should return success when user is logged in and services return success`() =
        runTest {
            // Given
            val profile = createTestProfile()
            val avatars = createTestAvatars()

            // Save token
            tokenStorage.saveToken(testToken)

            // Mock profile service
            val profileResult = Result.success(profile)
            coEvery { profileRepository.get() } returns profileResult

            // Mock avatar service
            val avatarResult = GravatarResult.Success<List<Avatar>, ErrorType>(avatars)
            coEvery {
                avatarService.retrieveCatching(testToken, match { it.toString() == testHash })
            } returns avatarResult

            // When
            val result = repository.getAvatars()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(avatars, result.getOrNull())

            // Verify interactions
            coVerify { profileRepository.get() }
            coVerify { avatarService.retrieveCatching(testToken, any<Hash>()) }
        }

    @Test
    fun `getAvatars should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clearToken()

        // When
        val result = repository.getAvatars()

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)

        // Verify no interactions with services
        coVerify(exactly = 0) { profileRepository.get() }
        coVerify(exactly = 0) {
            avatarService.retrieveCatching(
                any(),
                match { it.toString() == testHash }
            )
        }
    }

    @Test
    fun `getProfile should return success when user is logged in and service returns success`() =
        runTest {
            // Given
            val profile = createTestProfile()
            val profileResult = Result.success(profile)
            coEvery { profileRepository.get() } returns profileResult

            // When
            val result = repository.getProfile()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(profile, result.getOrNull())
            coVerify { profileRepository.get() }
        }

    @Test
    fun `getProfile should return failure when retrieveAuthenticatedCatching returns null profile`() =
        runTest {
            // Given
            val profileResult = Result.failure<Profile>(IllegalStateException("Test exception"))
            coEvery { profileRepository.get() } returns profileResult

            // When
            val result = repository.getProfile()

            // Then
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is IllegalStateException)
            coVerify { profileRepository.get() }
        }

    @Test
    fun `selectAvatar should return success when user is logged in and services return success`() =
        runTest {
            // Given
            val avatarId = "test-avatar-id"
            val profile = createTestProfile()

            // Save token
            tokenStorage.saveToken(testToken)

            // Mock profile service
            val profileResult = Result.success(profile)
            coEvery { profileRepository.get() } returns profileResult

            // Mock avatar service
            val avatarResult = GravatarResult.Success<Unit, ErrorType>(Unit)
            coEvery {
                avatarService.setAvatarCatching(
                    hash = testHash,
                    avatarId = avatarId,
                    oauthToken = testToken,
                )
            } returns avatarResult

            // When
            val result = repository.selectAvatar(avatarId)

            // Then
            assertTrue(result.isSuccess)

            // Verify interactions
            coVerify { profileRepository.get() }
            coVerify {
                avatarService.setAvatarCatching(
                    hash = testHash,
                    avatarId = avatarId,
                    oauthToken = testToken,
                )
            }
        }

    @Test
    fun `selectAvatar should return failure when user is not logged in`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        tokenStorage.clearToken()

        // When
        val result = repository.selectAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)

        // Verify no interactions with services
        coVerify(exactly = 0) { profileRepository.get() }
        coVerify(exactly = 0) { avatarService.setAvatarCatching(any(), any(), any()) }
    }

    @Test
    fun `selectAvatar should return failure when profile service returns null`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        tokenStorage.saveToken(testToken)

        val profileResult = Result.failure<Profile>(IllegalStateException("Test exception"))
        coEvery { profileRepository.get() } returns profileResult

        // When
        val result = repository.selectAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)

        // Verify interactions
        coVerify { profileRepository.get() }
        coVerify(exactly = 0) { avatarService.setAvatarCatching(any(), any(), any()) }
    }

    @Test
    fun `selectAvatar should return failure when avatar service returns failure`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        val profile = createTestProfile()

        // Save token
        tokenStorage.saveToken(testToken)

        // Mock profile service
        val profileResult = Result.success(profile)
        coEvery { profileRepository.get() } returns profileResult

        // Mock avatar service to return a result that is not a Success
        val avatarResult = mockk<GravatarResult<Unit, ErrorType>>()
        coEvery { avatarResult.valueOrNull() } returns null
        coEvery {
            avatarService.setAvatarCatching(
                hash = testHash,
                avatarId = avatarId,
                oauthToken = testToken,
            )
        } returns avatarResult

        // When
        val result = repository.selectAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Failed to select avatar", exception?.message)

        // Verify interactions
        coVerify { profileRepository.get() }
        coVerify {
            avatarService.setAvatarCatching(
                hash = testHash,
                avatarId = avatarId,
                oauthToken = testToken,
            )
        }
    }

    @Test
    fun `updateProfile should return success when profileRepository returns success`() =
        runTest {
            // Given
            val updateRequest = UpdateProfileRequest { }
            val updatedProfile = createTestProfile()
            val profileResult = Result.success(updatedProfile)
            coEvery { profileRepository.update(updateRequest) } returns profileResult

            // When
            val result = repository.updateProfile(updateRequest)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(updatedProfile, result.getOrNull())
            coVerify { profileRepository.update(updateRequest) }
        }

    @Test
    fun `updateProfile should return failure when profileRepository fails`() = runTest {
        // Given
        val updateRequest = UpdateProfileRequest { }
        coEvery {
            profileRepository.update(updateRequest)
        } returns Result.failure(IllegalStateException("Test exception"))

        // When
        val result = repository.updateProfile(updateRequest)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
    }

    @Test
    fun `uploadAvatar should return success when user is logged in and services return success`() =
        runTest {
            // Given
            val testFile = mockk<File>()
            val profile = createTestProfile()
            val uploadedAvatar = createAvatar(0)

            // Save token
            tokenStorage.saveToken(testToken)

            // Mock profile service
            val profileResult = Result.success(profile)
            coEvery { profileRepository.get() } returns profileResult

            // Mock avatar service
            val avatarResult = GravatarResult.Success<Avatar, ErrorType>(uploadedAvatar)
            coEvery {
                avatarService.uploadCatching(
                    file = testFile,
                    oauthToken = testToken,
                    hash = match { it.toString() == testHash }
                )
            } returns avatarResult

            // When
            val result = repository.uploadAvatar(testFile)

            // Then
            assertTrue(result is GravatarResult.Success)
            assertEquals(uploadedAvatar, result.valueOrNull())

            // Verify interactions
            coVerify {
                avatarService.uploadCatching(
                    file = testFile,
                    oauthToken = testToken,
                    hash = any<Hash>()
                )
            }
        }

    @Test
    fun `uploadAvatar should return failure when user is not logged in`() = runTest {
        // Given
        val testFile = mockk<File>()
        tokenStorage.clearToken()

        // When
        val result = repository.uploadAvatar(testFile)

        // Then
        assertTrue(result is GravatarResult.Failure)

        // Verify no interactions with services
        coVerify(exactly = 0) { profileRepository.get() }
        coVerify(exactly = 0) { avatarService.uploadCatching(any(), any(), any()) }
    }

    @Test
    fun `uploadAvatar should return failure when profileRepository returns null`() = runTest {
        // Given
        val testFile = mockk<File>()
        tokenStorage.saveToken(testToken)

        // Mock profile service to return null
        val profileResult = Result.failure<Profile>(IllegalStateException("Test exception"))
        coEvery { profileRepository.get() } returns profileResult

        // When
        val result = repository.uploadAvatar(testFile)

        // Then
        assertTrue(result is GravatarResult.Failure)

        // Verify interactions
        coVerify { profileRepository.get() }
        coVerify(exactly = 0) { avatarService.uploadCatching(any(), any(), any()) }
    }

    @Test
    fun `uploadAvatar should return failure when avatar service returns failure`() = runTest {
        // Given
        val testFile = mockk<File>()
        val profile = createTestProfile()

        // Save token
        tokenStorage.saveToken(testToken)

        // Mock profile service
        val profileResult = Result.success(profile)
        coEvery { profileRepository.get() } returns profileResult

        // Mock avatar service to return a result that is not a Success
        val avatarResult = GravatarResult.Failure<Avatar, ErrorType>(ErrorType.Server)
        coEvery {
            avatarService.uploadCatching(
                file = testFile,
                oauthToken = testToken,
                hash = match { it.toString() == testHash }
            )
        } returns avatarResult

        // When
        val result = repository.uploadAvatar(testFile)

        // Then
        assertTrue(result is GravatarResult.Failure)

        // Verify interactions
        coVerify { profileRepository.get() }
        coVerify {
            avatarService.uploadCatching(
                file = testFile,
                oauthToken = testToken,
                hash = any<Hash>()
            )
        }
    }

    @Test
    fun `deleteAvatar should return success when user is logged in and service returns success`() = runTest {
        // Given
        val avatarId = "test-avatar-id"

        // Save token
        tokenStorage.saveToken(testToken)

        // Mock avatar service
        val avatarResult = GravatarResult.Success<Unit, ErrorType>(Unit)
        coEvery {
            avatarService.deleteAvatarCatching(
                avatarId = avatarId,
                oauthToken = testToken
            )
        } returns avatarResult

        // When
        val result = repository.deleteAvatar(avatarId)

        // Then
        assertTrue(result.isSuccess)

        // Verify interactions
        coVerify {
            avatarService.deleteAvatarCatching(
                avatarId = avatarId,
                oauthToken = testToken
            )
        }
    }

    @Test
    fun `deleteAvatar should return failure when user is not logged in`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        tokenStorage.clearToken()

        // When
        val result = repository.deleteAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)

        // Verify no interactions with services
        coVerify(exactly = 0) { avatarService.deleteAvatarCatching(any(), any()) }
    }

    @Test
    fun `deleteAvatar should return failure when avatar service returns failure`() = runTest {
        // Given
        val avatarId = "test-avatar-id"

        // Save token
        tokenStorage.saveToken(testToken)

        // Mock avatar service to return a failure
        val errorType = ErrorType.Server
        val avatarResult = GravatarResult.Failure<Unit, ErrorType>(errorType)
        coEvery {
            avatarService.deleteAvatarCatching(
                avatarId = avatarId,
                oauthToken = testToken
            )
        } returns avatarResult

        // When
        val result = repository.deleteAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Failed to delete avatar: $errorType", exception?.message)

        // Verify interactions
        coVerify {
            avatarService.deleteAvatarCatching(
                avatarId = avatarId,
                oauthToken = testToken
            )
        }
    }

    private fun createTestProfile(): Profile {
        return Profile {
            hash = testHash
            displayName = "Test User"
            profileUrl = URI.create("https://example.com/profile")
            avatarUrl = URI.create("https://example.com/avatar")
            avatarAltText = ""
            location = "Test Location"
            description = "Test Description"
            jobTitle = "Test Job"
            company = "Test Company"
            verifiedAccounts = emptyList()
            pronouns = "they/them"
            pronunciation = "Test Pronunciation"
        }
    }

    private fun createTestAvatars(): List<Avatar> {
        return List(3) { index ->
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
