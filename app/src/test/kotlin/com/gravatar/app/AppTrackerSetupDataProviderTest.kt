package com.gravatar.app

import app.cash.turbine.test
import com.gravatar.analytics.TrackerSetupData
import com.gravatar.analytics.TrackingState
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.PrivacySettings
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetPrivacySettings
import com.gravatar.restapi.models.Profile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class AppTrackerSetupDataProviderTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var provider: AppTrackerSetupDataProvider

    private lateinit var privacySettingsFlow: MutableSharedFlow<PrivacySettings>
    private lateinit var profileFlow: MutableSharedFlow<Profile?>

    private val getPrivacySettings: GetPrivacySettings = object : GetPrivacySettings {
        override fun invoke() = privacySettingsFlow
    }

    private val userRepository: UserRepository = object : UserRepository {
        override suspend fun refreshProfile(): Result<Unit> = throw NotImplementedError()
        override suspend fun selectAvatar(avatarId: String): Result<Unit> = throw NotImplementedError()
        override suspend fun getAvatars() = throw NotImplementedError()
        override fun getProfile(): Flow<Profile?> = profileFlow
        override suspend fun updateProfile(
            updateRequest: com.gravatar.restapi.models.UpdateProfileRequest
        ): Result<Unit> =
            throw NotImplementedError()

        override suspend fun uploadAvatar(avatarFile: java.io.File) = throw NotImplementedError()
        override suspend fun deleteAvatar(avatarId: String): Result<Unit> = throw NotImplementedError()
    }

    @Before
    fun setup() {
        privacySettingsFlow = MutableSharedFlow()
        profileFlow = MutableSharedFlow()
        provider = AppTrackerSetupDataProvider(
            getPrivacySettings = getPrivacySettings,
            userRepository = userRepository,
        )
    }

    @Test
    fun `emits ENABLED when analytics enabled and non-null user id`() = runTest {
        // Given
        val profile = createProfile("user")

        provider.getTrackerSetupData().test {
            // When: emit both flows (combine requires both)
            privacySettingsFlow.emit(PrivacySettings(analyticsEnabled = true, crashReportingEnabled = true))
            profileFlow.emit(profile)

            // Then
            assertEquals(TrackerSetupData(TrackingState.ENABLED, "user"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits DISABLED when analytics disabled and null user Id when profile is null`() = runTest {
        provider.getTrackerSetupData().test {
            // When
            privacySettingsFlow.emit(PrivacySettings(analyticsEnabled = false, crashReportingEnabled = true))
            profileFlow.emit(null)

            // Then
            assertEquals(TrackerSetupData(TrackingState.DISABLED, null), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updates when privacy settings or user id changes and skips duplicate user id`() = runTest {
        // Given initial emissions
        val user1 = "user1"
        val user2 = "user2"
        val profile = createProfile(user1)

        provider.getTrackerSetupData().test {
            privacySettingsFlow.emit(PrivacySettings(analyticsEnabled = true, crashReportingEnabled = true))
            profileFlow.emit(profile)

            // First combined emission
            assertEquals(
                TrackerSetupData(trackingState = TrackingState.ENABLED, userId = user1),
                awaitItem()
            )

            // When: change privacy to disabled => new emission expected
            privacySettingsFlow.emit(PrivacySettings(analyticsEnabled = false, crashReportingEnabled = true))
            assertEquals(
                TrackerSetupData(trackingState = TrackingState.DISABLED, userId = user1),
                awaitItem()
            )

            // When: emit profile with same userId (1) => due to distinctUntilChanged on userIdFlow, no new emission from combine
            profileFlow.emit(profile)
            expectNoEvents()

            // When: emit profile with new userId (2) => new emission expected
            val profile2 = createProfile(user2)
            profileFlow.emit(profile2)
            assertEquals(
                TrackerSetupData(trackingState = TrackingState.DISABLED, userId = user2),
                awaitItem()
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createProfile(user: String): Profile {
        return Profile {
            firstName = "John"
            lastName = "Doe"
            displayName = "Johny"
            hash = "1234567890abcdef1234567890abcdef"
            location = "New York, USA"
            jobTitle = "Software Engineer"
            company = "Acme Inc."
            description = "A passionate software engineer with a love for coding and technology."
            verifiedAccounts = emptyList()
            profileUrl = URI.create("https://johndoe.com")
            avatarUrl = URI.create("https://www.gravatar.com/avatar/123")
            avatarAltText = "John Doe's Gravatar"
            pronouns = "he/him"
            pronunciation = "John Doe"
            verifiedAccounts = emptyList()
            userLogin = user
        }
    }
}
