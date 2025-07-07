package com.gravatar.app.usercomponent.data

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.services.ErrorType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

@ExperimentalCoroutinesApi
class RealProfileRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var repository: RealProfileRepository
    private val profileService = mockk<ProfileService>()
    private val tokenStorage = FakeAuthTokenStorage()

    private val testToken = "test-token"

    @Before
    fun setup() {
        repository = RealProfileRepository(
            profileService = profileService,
            tokenStorage = tokenStorage
        )
    }

    @Test
    fun `refreshUserProfile should return success when user is logged in and service returns success`() = runTest {
        // Given
        val profile = createTestProfile()
        tokenStorage.save(testToken)

        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

        // When
        val result = repository.refreshUserProfile()

        // Then
        assertTrue(result.isSuccess)
        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
    }

    @Test
    fun `refreshUserProfile should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clear()

        // When
        val result = repository.refreshUserProfile()

        // Then
        assertTrue(result.isFailure)

        // Verify no interactions with service
        coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
    }

    @Test
    fun `refreshUserProfile should return failure when service returns null`() = runTest {
        // Given
        tokenStorage.save(testToken)

        val profileResult = GravatarResult.Failure<Profile, ErrorType>(ErrorType.Server)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

        // When
        val result = repository.refreshUserProfile()

        // Then
        assertTrue(result.isFailure)

        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
    }

    @Test
    fun `get should return cached profile when available`() = runTest {
        // Given
        val profile = createTestProfile()
        tokenStorage.save(testToken)

        // First call to populate the cache
        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult
        repository.refreshUserProfile()

        // When
        val result = repository.get()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(profile, result.getOrNull())

        // Verify service was not called again
        coVerify(exactly = 1) { profileService.retrieveAuthenticatedCatching(any()) }
    }

    @Test
    fun `get should fetch profile when cache is empty`() = runTest {
        // Given
        val profile = createTestProfile()
        tokenStorage.save(testToken)

        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

        // When
        val result = repository.get()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(profile, result.getOrNull())

        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
    }

    @Test
    fun `get should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clear()

        // When
        val result = repository.get()

        // Then
        assertTrue(result.isFailure)

        // Verify no interactions with service
        coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
    }

    @Test
    fun `update should return success when user is logged in and service returns success`() = runTest {
        // Given
        val profile = createTestProfile()
        val updateRequest = UpdateProfileRequest {
            displayName = "New Name"
        }
        tokenStorage.save(testToken)

        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.updateProfileCatching(testToken, updateRequest) } returns profileResult

        // When
        val result = repository.update(updateRequest)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(profile, result.getOrNull())

        coVerify { profileService.updateProfileCatching(testToken, updateRequest) }
    }

    @Test
    fun `update should return failure when user is not logged in`() = runTest {
        // Given
        val updateRequest = UpdateProfileRequest {
            displayName = "New Name"
        }
        tokenStorage.clear()

        // When
        val result = repository.update(updateRequest)

        // Then
        assertTrue(result.isFailure)

        // Verify no interactions with service
        coVerify(exactly = 0) { profileService.updateProfileCatching(any(), any()) }
    }

    @Test
    fun `update should return failure when service returns null`() = runTest {
        // Given
        val updateRequest = UpdateProfileRequest {
            displayName = "New Name"
        }
        tokenStorage.save(testToken)

        val profileResult = GravatarResult.Failure<Profile, ErrorType>(ErrorType.Server)
        coEvery { profileService.updateProfileCatching(testToken, updateRequest) } returns profileResult

        // When
        val result = repository.update(updateRequest)

        // Then
        assertTrue(result.isFailure)

        coVerify { profileService.updateProfileCatching(testToken, updateRequest) }
    }

    @Test
    fun `delete should clear cached profile`() = runTest {
        // Given
        val profile = createTestProfile()
        tokenStorage.save(testToken)

        // First populate the cache
        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult
        repository.get()

        // When
        repository.delete()

        // Then - verify that get() now has to fetch from service again
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult
        repository.get()

        // Verify service was called twice (once before delete and once after)
        coVerify(exactly = 2) { profileService.retrieveAuthenticatedCatching(testToken) }
    }

    private fun createTestProfile(): Profile {
        return Profile {
            hash = "test-hash"
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
}
