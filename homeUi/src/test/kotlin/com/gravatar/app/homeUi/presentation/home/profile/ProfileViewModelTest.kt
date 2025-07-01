package com.gravatar.app.homeUi.presentation.home.profile

import app.cash.turbine.test
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutEditorField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val userRepository = mockk<UserRepository>()

    private lateinit var viewModel: ProfileViewModel

    @Test
    fun `when viewmodel is initialized and fetch profile finishes successfully then uiState contains profile`() =
        runTest {
            val profile = profile()

            coEvery {
                userRepository.getProfile()
            } returns Result.success(profile)

            viewModel = initViewModel()

            viewModel.uiState.test {
                assertEquals(ProfileUiState(isLoading = false), awaitItem())
                assertEquals(ProfileUiState(isLoading = true), awaitItem())
                assertEquals(
                    ProfileUiState(isLoading = false, profile = profile),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when viewmodel is initialized and fetch profile fails then uiState has no profile`() =
        runTest {
            val exception = IllegalStateException("Failed to retrieve profile")

            coEvery {
                userRepository.getProfile()
            } returns Result.failure(exception)

            viewModel = initViewModel()

            viewModel.uiState.test {
                assertEquals(ProfileUiState(isLoading = false), awaitItem())
                assertEquals(ProfileUiState(isLoading = true), awaitItem())
                assertEquals(
                    ProfileUiState(isLoading = false, profile = null),
                    awaitItem()
                )
            }
        }

    @Test
    fun `when profile is available then aboutFields are correctly populated`() {
        // Given
        val profile = profile()

        // When
        val aboutFields = profile.aboutFields()

        // Then
        assertTrue("aboutFields should not be empty", aboutFields.isNotEmpty())

        // Verify all fields are correctly populated
        val displayNameField = aboutFields.find { it.type == AboutInputField.DISPLAY_NAME }
        assertEquals("DisplayName should match mock profile", profile.displayName, displayNameField?.value)

        val aboutMeField = aboutFields.find { it.type == AboutInputField.ABOUT_ME }
        assertEquals("AboutMe should match mock profile", profile.description, aboutMeField?.value)

        val pronounsField = aboutFields.find { it.type == AboutInputField.PRONOUNS }
        assertEquals("Pronouns should match mock profile", profile.pronouns, pronounsField?.value)

        val pronunciationField = aboutFields.find { it.type == AboutInputField.PRONUNCIATION }
        assertEquals("Pronunciation should match mock profile", profile.pronunciation, pronunciationField?.value)

        val locationField = aboutFields.find { it.type == AboutInputField.LOCATION }
        assertEquals("Location should match mock profile", profile.location, locationField?.value)

        val jobTitleField = aboutFields.find { it.type == AboutInputField.JOB_TITLE }
        assertEquals("JobTitle should match mock profile", "Software Engineer", jobTitleField?.value)

        val companyField = aboutFields.find { it.type == AboutInputField.COMPANY }
        assertEquals("Company should match mock profile", "Test Company", companyField?.value)

        val firstNameField = aboutFields.find { it.type == AboutInputField.FIRST_NAME }
        assertEquals("FirstName should match mock profile", profile.firstName.orEmpty(), firstNameField?.value)

        val lastNameField = aboutFields.find { it.type == AboutInputField.LAST_NAME }
        assertEquals("LastName should match mock profile", profile.lastName.orEmpty(), lastNameField?.value)

        val cellPhoneField = aboutFields.find { it.type == AboutInputField.CELL_PHONE }
        assertEquals("CellPhone should match mock profile", profile.contactInfo?.cellPhone, cellPhoneField?.value)

        val contactEmailField = aboutFields.find { it.type == AboutInputField.CONTACT_EMAIL }
        assertEquals("ContactEmail should match mock profile", profile.contactInfo?.email, contactEmailField?.value)
    }

    @Test
    fun `when OnProfileFieldUpdated event with modified value then field is added to editedAboutFields`() =
        runTest {
            // Given
            val profile = profile()
            coEvery {
                userRepository.getProfile()
            } returns Result.success(profile)
            viewModel = initViewModel()

            advanceUntilIdle()

            val modifiedField = AboutEditorField(
                type = AboutInputField.DISPLAY_NAME,
                value = "Modified Name" // Different from "John Doe" in the profile
            )

            // When
            viewModel.onEvent(ProfileEvent.OnProfileFieldUpdated(modifiedField))

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("Modified Name", state.editedAboutFields[AboutInputField.DISPLAY_NAME])
                assertTrue(state.hasUnsavedChanges)
            }
        }

    @Test
    fun `when OnProfileFieldUpdated event with original value then field is removed from editedAboutFields`() =
        runTest {
            // Given
            val profile = profile()
            coEvery {
                userRepository.getProfile()
            } returns Result.success(profile)
            viewModel = initViewModel()

            advanceUntilIdle()

            val modifiedField = AboutEditorField(
                type = AboutInputField.DISPLAY_NAME,
                value = "Modified Name"
            )
            viewModel.onEvent(ProfileEvent.OnProfileFieldUpdated(modifiedField))

            val originalField = AboutEditorField(
                type = AboutInputField.DISPLAY_NAME,
                value = profile.displayName
            )
            viewModel.onEvent(ProfileEvent.OnProfileFieldUpdated(originalField))

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(
                    "Field should be removed from editedAboutFields",
                    state.editedAboutFields[AboutInputField.DISPLAY_NAME] == null
                )
                assertEquals(false, state.hasUnsavedChanges)
            }
        }

    @Test
    fun `updateProfileRequest should map all fields correctly`() {
        // Given
        val fieldValues = mapOf(
            AboutInputField.DISPLAY_NAME to "Test Display Name",
            AboutInputField.ABOUT_ME to "Test About Me",
            AboutInputField.PRONOUNS to "Test Pronouns",
            AboutInputField.PRONUNCIATION to "Test Pronunciation",
            AboutInputField.LOCATION to "Test Location",
            AboutInputField.JOB_TITLE to "Test Job Title",
            AboutInputField.COMPANY to "Test Company",
            AboutInputField.FIRST_NAME to "Test First Name",
            AboutInputField.LAST_NAME to "Test Last Name",
            AboutInputField.CELL_PHONE to "Test Cell Phone",
            AboutInputField.CONTACT_EMAIL to "test@example.com"
        )

        // When
        val updateRequest = fieldValues.updateProfileRequest()

        // Then
        assertEquals("Test Display Name", updateRequest.displayName)
        assertEquals("Test About Me", updateRequest.description)
        assertEquals("Test Pronouns", updateRequest.pronouns)
        assertEquals("Test Pronunciation", updateRequest.pronunciation)
        assertEquals("Test Location", updateRequest.location)
        assertEquals("Test Job Title", updateRequest.jobTitle)
        assertEquals("Test Company", updateRequest.company)
        assertEquals("Test First Name", updateRequest.firstName)
        assertEquals("Test Last Name", updateRequest.lastName)
        assertEquals("Test Cell Phone", updateRequest.cellPhone)
        assertEquals("test@example.com", updateRequest.contactEmail)
    }

    @Test
    fun `when saveChanges succeeds then profile is updated and editedAboutFields are cleared`() = runTest {
        // Given
        val originalProfile = profile()
        val updatedProfile = profile(displayName = "Updated Name")

        coEvery {
            userRepository.getProfile()
        } returns Result.success(originalProfile)

        viewModel = initViewModel()
        advanceUntilIdle()

        val modifiedField = AboutEditorField(
            type = AboutInputField.DISPLAY_NAME,
            value = "Updated Name"
        )
        viewModel.onEvent(ProfileEvent.OnProfileFieldUpdated(modifiedField))

        coEvery {
            userRepository.updateProfile(any())
        } returns Result.success(updatedProfile)

        // When
        viewModel.onEvent(ProfileEvent.OnSaveClicked)

        viewModel.uiState.test {
            var expectedUiState = ProfileUiState(
                isLoading = false,
                profile = originalProfile,
                editedAboutFields = mapOf(AboutInputField.DISPLAY_NAME to "Updated Name"),
                isSavingProfile = false,
            )
            assertEquals(expectedUiState, awaitItem())

            expectedUiState = expectedUiState.copy(isSavingProfile = true)
            assertEquals(expectedUiState, awaitItem())

            expectedUiState = expectedUiState.copy(
                isSavingProfile = false,
                profile = updatedProfile,
                editedAboutFields = emptyMap(),
            )
            assertEquals(expectedUiState, awaitItem())
        }
        coVerify(exactly = (1)) { userRepository.updateProfile(any()) }
    }

    @Test
    fun `when saveChanges fails then state remains unchanged`() = runTest {
        // Given
        val originalProfile = profile()
        val exception = IllegalStateException("Update failed")

        coEvery {
            userRepository.getProfile()
        } returns Result.success(originalProfile)

        viewModel = initViewModel()
        advanceUntilIdle()

        val modifiedField = AboutEditorField(
            type = AboutInputField.DISPLAY_NAME,
            value = "Updated Name"
        )
        viewModel.onEvent(ProfileEvent.OnProfileFieldUpdated(modifiedField))

        coEvery {
            userRepository.updateProfile(any())
        } returns Result.failure(exception)

        // When
        viewModel.onEvent(ProfileEvent.OnSaveClicked)

        // Then
        viewModel.uiState.test {
            var expectedUiState = ProfileUiState(
                isLoading = false,
                profile = originalProfile,
                editedAboutFields = mapOf(AboutInputField.DISPLAY_NAME to "Updated Name"),
                isSavingProfile = false,
            )
            assertEquals(expectedUiState, awaitItem())

            expectedUiState = expectedUiState.copy(isSavingProfile = true)
            assertEquals(expectedUiState, awaitItem())

            expectedUiState = expectedUiState.copy(
                isSavingProfile = false,
            )
            assertEquals(expectedUiState, awaitItem())
        }
        coVerify(exactly = (1)) { userRepository.updateProfile(any()) }
    }

    private fun profile(displayName: String = "John Doe") = Profile {
        hash = "mock-hash"
        this.displayName = displayName
        profileUrl = URI("https://www.gravatar.com/mock-hash")
        avatarUrl = URI("https://www.gravatar.com/avatar/mock-hash")
        avatarAltText = "Avatar for John Doe"
        description = "My description"
        pronouns = "My pronouns"
        pronunciation = "My pronunciation"
        location = "San Francisco, CA"
        jobTitle = "Software Engineer"
        company = "Test Company"
        firstName = "John"
        lastName = "Doe"
        verifiedAccounts = emptyList()
        contactInfo = ProfileContactInfo {
            cellPhone = "123-456-7890"
            email = "gravatar@automattic.com"
        }
    }

    private fun initViewModel() = ProfileViewModel(userRepository)
}
