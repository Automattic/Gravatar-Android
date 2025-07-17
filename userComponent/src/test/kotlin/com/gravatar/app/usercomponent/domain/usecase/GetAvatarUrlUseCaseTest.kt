package com.gravatar.app.usercomponent.domain.usecase

import app.cash.turbine.test
import com.gravatar.AvatarQueryOptions
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
import kotlinx.coroutines.flow.flow
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

    private val hash = "test-hash"
    val avatarUrl = AvatarUrl(
        hash = Hash(hash),
        avatarQueryOptions = AvatarQueryOptions.Builder()
            .setPreferredSize(512)
            .build()
    )

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
        val profile = createProfile(hash)
        coEvery { profileRepository.get() } returns flow { emit(profile) }

        // When & Then
        getAvatarUrlUseCase().test {
            val expectedUrl = avatarUrl.url(null)
            assertEquals(expectedUrl, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `invoke should return null when profile repository returns null`() = runTest {
        // Given
        coEvery { profileRepository.get() } returns flow { emit(null) }

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
        coEvery { profileRepository.get() } returns flow { emit(profile) }

        // When & Then
        getAvatarUrlUseCase().test {
            val initialUrl = avatarUrl.url(null)
            assertEquals(initialUrl, awaitItem())

            // Update cache buster
            val cacheBuster = "cache-buster-1"
            cacheBusterFlow.emit(cacheBuster)

            // Should emit new URL with cache buster
            val updatedUrl = avatarUrl.url(cacheBuster)
            assertEquals(updatedUrl, awaitItem())

            // Update cache buster again
            val newCacheBuster = "cache-buster-2"
            cacheBusterFlow.emit(newCacheBuster)

            // Should emit new URL with new cache buster
            val newUrl = avatarUrl.url(newCacheBuster)
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
