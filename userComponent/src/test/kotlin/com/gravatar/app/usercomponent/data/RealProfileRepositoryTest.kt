package com.gravatar.app.usercomponent.data

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.database.ProfileDao
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import com.gravatar.app.usercomponent.data.database.model.ProfileWithVerifiedAccounts
import com.gravatar.app.usercomponent.data.database.model.toEntity
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import com.gravatar.restapi.models.UpdateProfileRequest
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
    private val profileDao = mockk<ProfileDao>()

    private val testToken = "test-token"

    @Before
    fun setup() {
        repository = RealProfileRepository(
            profileService = profileService,
            tokenStorage = tokenStorage,
            profileDao = profileDao
        )
    }

    @Test
    fun `refreshUserProfile should return success when user is logged in and service returns success`() = runTest {
        // Given
        val profile = createTestProfile()
        tokenStorage.saveToken(testToken)

        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult
        coJustRun { profileDao.insertProfileWithVerifiedAccounts(any(), any()) }

        // When
        val result = repository.refreshUserProfile()

        // Then
        assertTrue(result.isSuccess)
        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
        coVerify { profileDao.insertProfileWithVerifiedAccounts(any(), any()) }
    }

    @Test
    fun `refreshUserProfile should return failure when user is not logged in`() = runTest {
        // Given
        tokenStorage.clearToken()

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
        tokenStorage.saveToken(testToken)

        val profileResult = GravatarResult.Failure<Profile, ErrorType>(ErrorType.Server)
        coEvery { profileService.retrieveAuthenticatedCatching(testToken) } returns profileResult

        // When
        val result = repository.refreshUserProfile()

        // Then
        assertTrue(result.isFailure)

        coVerify { profileService.retrieveAuthenticatedCatching(testToken) }
    }

    @Test
    fun `get should return profile from database when available`() = runTest {
        // Given
        val profile = createTestProfile()
        val profileEntity = ProfileEntity.fromProfile(profile)
        val profileWithVerifiedAccounts = ProfileWithVerifiedAccounts(
            profile = profileEntity,
            verifiedAccounts = profile.verifiedAccounts.map { it.toEntity(profileEntity.userId) }
        )
        tokenStorage.saveToken(testToken)

        every { profileDao.getProfileWithVerifiedAccounts() } returns flow { emit(profileWithVerifiedAccounts) }

        // When
        repository.get().test {
            // Then
            assertEquals(profile, awaitItem())
            awaitComplete()
            // Verify DAO was called and service was not called
            verify { profileDao.getProfileWithVerifiedAccounts() }
            coVerify(exactly = 0) { profileService.retrieveAuthenticatedCatching(any()) }
        }
    }

    @Test
    fun `update should return success when user is logged in and service returns success`() = runTest {
        // Given
        val profile = createTestProfile()
        val updateRequest = UpdateProfileRequest {
            displayName = "New Name"
        }
        tokenStorage.saveToken(testToken)

        val profileResult = GravatarResult.Success<Profile, ErrorType>(profile)
        coEvery { profileService.updateProfileCatching(testToken, updateRequest) } returns profileResult
        coJustRun { profileDao.insertProfileWithVerifiedAccounts(any(), any()) }

        // When
        val result = repository.update(updateRequest)

        // Then
        assertTrue(result.isSuccess)

        coVerify { profileService.updateProfileCatching(testToken, updateRequest) }
        coVerify { profileDao.insertProfileWithVerifiedAccounts(any(), any()) }
    }

    @Test
    fun `update should return failure when user is not logged in`() = runTest {
        // Given
        val updateRequest = UpdateProfileRequest {
            displayName = "New Name"
        }
        tokenStorage.clearToken()

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
        tokenStorage.saveToken(testToken)

        val profileResult = GravatarResult.Failure<Profile, ErrorType>(ErrorType.Server)
        coEvery { profileService.updateProfileCatching(testToken, updateRequest) } returns profileResult

        // When
        val result = repository.update(updateRequest)

        // Then
        assertTrue(result.isFailure)

        coVerify { profileService.updateProfileCatching(testToken, updateRequest) }
    }

    @Test
    fun `delete should clear profile from database`() = runTest {
        // Given
        coJustRun { profileDao.delete() }

        // When
        repository.delete()

        // Then
        coVerify { profileDao.delete() }
    }

    private fun createTestProfile(): Profile {
        return Profile {
            userId = 123
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
            contactInfo = ProfileContactInfo {}
        }
    }
}
