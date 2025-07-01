package com.gravatar.app.usercomponent.data

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.restapi.models.Avatar
import com.gravatar.restapi.models.Profile
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
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
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class RealUserRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var repository: RealUserRepository
    private val avatarService = mockk<AvatarService>()
    private val profileService = mockk<ProfileService>()
    private val tokenStorage = FakeAuthTokenStorage()

    private val testToken = "test-token"
    private val testHash = "test-hash"

    @Before
    fun setup() {
        repository = RealUserRepository(
            avatarService = avatarService,
            profileService = profileService,
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
            tokenStorage.save(testToken)

            // Mock profile service
            val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
            coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

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
            coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
            coVerify { avatarService.retrieveCatching(testToken, any<Hash>()) }
        }

    @Test
    fun `getAvatars should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clear()

        // When
        val result = repository.getAvatars()

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)

        // Verify no interactions with services
        coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
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
            tokenStorage.save(testToken)
            val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
            coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

            // When
            val result = repository.getProfile()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(profile, result.getOrNull())
            coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
        }

    @Test
    fun `getProfile should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clear()

        // When
        val result = repository.getProfile()

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)
        coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
    }

    @Test
    fun `getProfile should return failure when retrieveAuthenticatedCatching returns null profile`() =
        runTest {
            // Given
            tokenStorage.save(testToken)
            val profileResult = mockk<GravatarResult<Profile, ErrorType>>()
            coEvery { profileResult.valueOrNull() } returns null
            coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

            // When
            val result = repository.getProfile()

            // Then
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is IllegalStateException)
            assertEquals("Failed to retrieve profile", exception?.message)
            coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
        }

    @Test
    fun `selectAvatar should return success when user is logged in and services return success`() =
        runTest {
            // Given
            val avatarId = "test-avatar-id"
            val profile = createTestProfile()

            // Save token
            tokenStorage.save(testToken)

            // Mock profile service
            val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
            coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

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
            coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
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
        tokenStorage.clear()

        // When
        val result = repository.selectAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("User is not logged in", exception?.message)

        // Verify no interactions with services
        coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
        coVerify(exactly = 0) { avatarService.setAvatarCatching(any(), any(), any()) }
    }

    @Test
    fun `selectAvatar should return failure when profile service returns null`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        tokenStorage.save(testToken)

        // Mock profile service to return null
        val profileResult = mockk<GravatarResult<Profile, ErrorType>>()
        coEvery { profileResult.valueOrNull() } returns null
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

        // When
        val result = repository.selectAvatar(avatarId)

        // Then
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Failed to select avatar", exception?.message)

        // Verify interactions
        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
        coVerify(exactly = 0) { avatarService.setAvatarCatching(any(), any(), any()) }
    }

    @Test
    fun `selectAvatar should return failure when avatar service returns failure`() = runTest {
        // Given
        val avatarId = "test-avatar-id"
        val profile = createTestProfile()

        // Save token
        tokenStorage.save(testToken)

        // Mock profile service
        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

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
        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
        coVerify {
            avatarService.setAvatarCatching(
                hash = testHash,
                avatarId = avatarId,
                oauthToken = testToken,
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
