package com.gravatar.app.homeUi.presentation.home.share

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.app.usercomponent.domain.usecase.GetUserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.UpdateUserSharePreferences
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URI
import java.net.URL

@OptIn(ExperimentalCoroutinesApi::class)
class ShareViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val getAvatarUrl: GetAvatarUrl = object : GetAvatarUrl {
        override fun invoke() = avatarUrlFlow
    }
    private val getUserSharePreferences: GetUserSharePreferences = object : GetUserSharePreferences {
        override fun invoke() = userSharePreferencesFlow
    }
    private val updateUserSharePreferences = object : UpdateUserSharePreferences {
        override suspend fun invoke(userSharePreferences: UserSharePreferences) {
            userSharePreferencesFlow.emit(userSharePreferences)
        }
    }
    private val userRepository = mockk<UserRepository>()

    private lateinit var viewModel: ShareViewModel

    private val avatarUrlFlow: MutableSharedFlow<URL?> = MutableSharedFlow()
    private val profileFlow: MutableSharedFlow<Profile?> = MutableSharedFlow()
    private val userSharePreferencesFlow: MutableSharedFlow<UserSharePreferences> = MutableSharedFlow()

    @Before
    fun setup() {
        every { userRepository.getProfile() } returns profileFlow
        viewModel = ShareViewModel(userRepository, getAvatarUrl, getUserSharePreferences, updateUserSharePreferences)
    }

    @Test
    fun `when viewmodel is initialized then uiState has default values`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(ShareUiState(), initialState)
        }
    }

    @Test
    fun `when profile is emitted then uiState is updated with profile`() = runTest {
        // Given
        val testProfile = createTestProfile()

        // When
        profileFlow.emit(testProfile)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testProfile, state.profile)
        }
    }

    @Test
    fun `when OnEmailValueChanged event is triggered then emailValue is updated`() = runTest {
        // Given
        val newEmailValue = "test@example.com"

        // When
        viewModel.onEvent(ShareEvent.OnEmailValueChanged(newEmailValue))

        // Then
        viewModel.uiState.test {
            assertEquals(newEmailValue, awaitItem().privateContactState.emailValue)
        }
    }

    @Test
    fun `when OnPhoneValueChanged event is triggered then phoneValue is updated`() = runTest {
        // Given
        val newPhoneValue = "123-456-7890"

        // When
        viewModel.onEvent(ShareEvent.OnPhoneValueChanged(newPhoneValue))

        // Then
        viewModel.uiState.test {
            assertEquals(newPhoneValue, awaitItem().privateContactState.phoneValue)
        }
    }

    @Test
    fun `when OnAboutAppClicked event is triggered then isAboutAppDialogVisible is set to true`() = runTest {
        // When
        viewModel.onEvent(ShareEvent.OnAboutAppClicked)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAboutAppDialogVisible)
        }
    }

    @Test
    fun `when OnDismissAboutAppDialog event is triggered then isAboutAppDialogVisible is set to false`() = runTest {
        // First show the dialog
        viewModel.onEvent(ShareEvent.OnAboutAppClicked)
        advanceUntilIdle()

        // Verify dialog is visible
        viewModel.uiState.test {
            assertTrue(awaitItem().isAboutAppDialogVisible)

            // When
            viewModel.onEvent(ShareEvent.OnDismissAboutAppDialog)

            // Then
            assertFalse(awaitItem().isAboutAppDialogVisible)
        }
    }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with Name field then name preference is updated`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newNamePreference = !initialState.userSharePreferences.name
        val shareFieldType = ShareFieldType.Name(checked = newNamePreference)

        // When
        viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

        // Then
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(newNamePreference, updatedState.userSharePreferences.name)
            // Verify other preferences remain unchanged
            assertEquals(initialState.userSharePreferences.location, updatedState.userSharePreferences.location)
            assertEquals(initialState.userSharePreferences.title, updatedState.userSharePreferences.title)
            assertEquals(initialState.userSharePreferences.organization, updatedState.userSharePreferences.organization)
            assertEquals(initialState.userSharePreferences.description, updatedState.userSharePreferences.description)
            assertEquals(initialState.userSharePreferences.profileUrl, updatedState.userSharePreferences.profileUrl)
        }
    }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with Location field then location preference is updated`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newLocationPreference = !initialState.userSharePreferences.location
        val shareFieldType = ShareFieldType.Location(checked = newLocationPreference)

        // When
        viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

        // Then
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(newLocationPreference, updatedState.userSharePreferences.location)
            // Verify other preferences remain unchanged
            assertEquals(initialState.userSharePreferences.name, updatedState.userSharePreferences.name)
            assertEquals(initialState.userSharePreferences.title, updatedState.userSharePreferences.title)
            assertEquals(initialState.userSharePreferences.organization, updatedState.userSharePreferences.organization)
            assertEquals(initialState.userSharePreferences.description, updatedState.userSharePreferences.description)
            assertEquals(initialState.userSharePreferences.profileUrl, updatedState.userSharePreferences.profileUrl)
        }
    }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with Title field then title preference is updated`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newTitlePreference = !initialState.userSharePreferences.title
        val shareFieldType = ShareFieldType.Title(checked = newTitlePreference)

        // When
        viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

        // Then
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(newTitlePreference, updatedState.userSharePreferences.title)
            // Verify other preferences remain unchanged
            assertEquals(initialState.userSharePreferences.name, updatedState.userSharePreferences.name)
            assertEquals(initialState.userSharePreferences.location, updatedState.userSharePreferences.location)
            assertEquals(initialState.userSharePreferences.organization, updatedState.userSharePreferences.organization)
            assertEquals(initialState.userSharePreferences.description, updatedState.userSharePreferences.description)
            assertEquals(initialState.userSharePreferences.profileUrl, updatedState.userSharePreferences.profileUrl)
        }
    }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with Organization field then organization preference is updated`() =
        runTest {
            // Given
            val initialState = viewModel.uiState.value
            val newOrganizationPreference = !initialState.userSharePreferences.organization
            val shareFieldType = ShareFieldType.Organization(checked = newOrganizationPreference)

            // When
            viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

            // Then
            viewModel.uiState.test {
                val updatedState = awaitItem()
                assertEquals(newOrganizationPreference, updatedState.userSharePreferences.organization)
                // Verify other preferences remain unchanged
                assertEquals(initialState.userSharePreferences.name, updatedState.userSharePreferences.name)
                assertEquals(initialState.userSharePreferences.location, updatedState.userSharePreferences.location)
                assertEquals(initialState.userSharePreferences.title, updatedState.userSharePreferences.title)
                assertEquals(
                    initialState.userSharePreferences.description,
                    updatedState.userSharePreferences.description
                )
                assertEquals(initialState.userSharePreferences.profileUrl, updatedState.userSharePreferences.profileUrl)
            }
        }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with Description field then description preference is updated`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newDescriptionPreference = !initialState.userSharePreferences.description
        val shareFieldType = ShareFieldType.Description(checked = newDescriptionPreference)

        // When
        viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

        // Then
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(newDescriptionPreference, updatedState.userSharePreferences.description)
            // Verify other preferences remain unchanged
            assertEquals(initialState.userSharePreferences.name, updatedState.userSharePreferences.name)
            assertEquals(initialState.userSharePreferences.location, updatedState.userSharePreferences.location)
            assertEquals(initialState.userSharePreferences.title, updatedState.userSharePreferences.title)
            assertEquals(initialState.userSharePreferences.organization, updatedState.userSharePreferences.organization)
            assertEquals(initialState.userSharePreferences.profileUrl, updatedState.userSharePreferences.profileUrl)
        }
    }

    @Test
    fun `when OnUserSharePreferencesChanged event is triggered with ProfileUrl field then profileUrl preference is updated`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newProfileUrlPreference = !initialState.userSharePreferences.profileUrl
        val shareFieldType = ShareFieldType.ProfileUrl(checked = newProfileUrlPreference)

        // When
        viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

        // Then
        viewModel.uiState.test {
            val updatedState = awaitItem()
            assertEquals(newProfileUrlPreference, updatedState.userSharePreferences.profileUrl)
            // Verify other preferences remain unchanged
            assertEquals(initialState.userSharePreferences.name, updatedState.userSharePreferences.name)
            assertEquals(initialState.userSharePreferences.location, updatedState.userSharePreferences.location)
            assertEquals(initialState.userSharePreferences.title, updatedState.userSharePreferences.title)
            assertEquals(initialState.userSharePreferences.organization, updatedState.userSharePreferences.organization)
            assertEquals(initialState.userSharePreferences.description, updatedState.userSharePreferences.description)
        }
    }

    @Test
    fun `when user share preferences are emitted then uiState is updated with preferences`() = runTest {
        // Given
        val testPreferences = UserSharePreferences.Default.copy(
            name = false,
            title = false,
            description = false,
        )

        // When
        userSharePreferencesFlow.emit(testPreferences)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testPreferences, state.userSharePreferences)
        }
    }

    @Test
    fun `when user share preferences are changed then updateUserSharePreferences is called`() = runTest {
        // Given
        val initialState = viewModel.uiState.value
        val newNamePreference = !initialState.userSharePreferences.name
        val shareFieldType = ShareFieldType.Name(checked = newNamePreference)

        userSharePreferencesFlow.test {
            // When
            viewModel.onEvent(ShareEvent.OnUserSharePreferencesChanged(shareFieldType))

            // Then
            val expectedSharePreferences = initialState.userSharePreferences.copy(
                name = newNamePreference,
            )
            assertEquals(expectedSharePreferences, awaitItem())
        }
    }

    @Test
    fun `when OnPrivateInformationClicked event is triggered then isPrivateInformationDialogVisible is set to true`() = runTest {
        // When
        viewModel.onEvent(ShareEvent.OnPrivateInformationClicked)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isPrivateInformationDialogVisible)
        }
    }

    @Test
    fun `when OnDismissPrivateInformationDialog event is triggered then isPrivateInformationDialogVisible is set to false`() = runTest {
        // First show the dialog
        viewModel.onEvent(ShareEvent.OnPrivateInformationClicked)
        advanceUntilIdle()

        // Verify dialog is visible
        viewModel.uiState.test {
            assertTrue(awaitItem().isPrivateInformationDialogVisible)

            // When
            viewModel.onEvent(ShareEvent.OnDismissPrivateInformationDialog)

            // Then
            assertFalse(awaitItem().isPrivateInformationDialogVisible)
        }
    }

    private fun createTestProfile() = Profile {
        hash = "test-hash"
        displayName = "Test User"
        profileUrl = URI("https://www.gravatar.com/test-hash")
        avatarUrl = URI("https://www.gravatar.com/avatar/test-hash")
        avatarAltText = "Avatar for Test User"
        description = "Test description"
        pronouns = "They/Them"
        pronunciation = "Test pronunciation"
        location = "Test Location"
        jobTitle = "Software Engineer"
        company = "Test Company"
        firstName = "Test"
        lastName = "User"
        verifiedAccounts = emptyList()
        contactInfo = ProfileContactInfo {
            cellPhone = "123-456-7890"
            email = "test@example.com"
        }
    }
}
