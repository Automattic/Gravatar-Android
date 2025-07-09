package com.gravatar.app.usercomponent.domain.usecase

import app.cash.turbine.test
import com.gravatar.AvatarUrl
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.restapi.models.Profile
import com.gravatar.types.Hash
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

@ExperimentalCoroutinesApi
class GetAvatarUrlUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var getAvatarUrlUseCase: GetAvatarUrlUseCase
    private val profileRepository = mockk<ProfileRepository>()
    private val cacheBusterFlow = MutableStateFlow<String?>(null)
    private val avatarCacheBusterStorage = object : AvatarCacheBusterStorage {
        override fun getAvatarCacheBuster() = cacheBusterFlow

        override suspend fun saveAvatarCacheBuster(value: String) {
            cacheBusterFlow.emit(value)
        }
    }

    @Before
    fun setup() {
        getAvatarUrlUseCase = GetAvatarUrlUseCase(
            profileRepository = profileRepository,
            avatarCacheBusterStorage = avatarCacheBusterStorage
        )
    }

    @Test
    fun `invoke should return URL when profile repository returns a valid hash`() = runTest {
        // Given
        val hash = "test-hash"
        val profile = createProfile(hash)
        coEvery { profileRepository.get() } returns Result.success(profile)

        // When & Then
        getAvatarUrlUseCase().test {
            val expectedUrl = AvatarUrl(Hash(hash)).url(null)
            assertEquals(expectedUrl, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `invoke should return null when profile repository returns null`() = runTest {
        // Given
        coEvery { profileRepository.get() } returns Result.failure(RuntimeException("Test exception"))

        // When & Then
        getAvatarUrlUseCase().test {
            assertNull(awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `invoke should return updated URL when cache buster changes`() = runTest {
        // Given
        val hash = "test-hash"
        val profile = createProfile(hash)
        coEvery { profileRepository.get() } returns Result.success(profile)

        // When & Then
        getAvatarUrlUseCase().test {
            val initialUrl = AvatarUrl(Hash(hash)).url(null)
            assertEquals(initialUrl, awaitItem())

            // Update cache buster
            val cacheBuster = "cache-buster-1"
            cacheBusterFlow.emit(cacheBuster)

            // Should emit new URL with cache buster
            val updatedUrl = AvatarUrl(Hash(hash)).url(cacheBuster)
            assertEquals(updatedUrl, awaitItem())

            // Update cache buster again
            val newCacheBuster = "cache-buster-2"
            cacheBusterFlow.emit(newCacheBuster)

            // Should emit new URL with new cache buster
            val newUrl = AvatarUrl(Hash(hash)).url(newCacheBuster)
            assertEquals(newUrl, awaitItem())

            // No more items should be emitted
            expectNoEvents()
        }
    }

    private fun createProfile(hash: String) = Profile {
        this.hash = hash
        displayName = "John"
        profileUrl = URI("https://www.gravatar.com/mock-hash")
        avatarUrl = URI("https://www.gravatar.com/avatar/mock-hash")
        avatarAltText = "Avatar for John Doe"
        description = "My description"
        pronouns = "My pronouns"
        pronunciation = "My pronunciation"
        location = "San Francisco, CA"
        jobTitle = "Software Engineer"
        company = "Test Company"
        verifiedAccounts = emptyList()
    }
}
