package com.gravatar.app.homeUi.presentation.home.profile

import app.cash.turbine.test
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.restapi.models.Profile
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.net.URI

class ProfileViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val profileService = mockk<ProfileService>()

    private lateinit var viewModel: ProfileViewModel

    @Test
    fun `when viewmodel is initialized and fetch profile finishes successfully then uiState contains profile`() =
        runTest {
            val profile = profile()

            coEvery {
                profileService.retrieveCatching(any<String>())
            } returns GravatarResult.Success(profile)

            viewModel = initViewModel()

            viewModel.uiState.test {
                assertEquals(ProfileUiState(isLoading = false), awaitItem())
                assertEquals(ProfileUiState(isLoading = true), awaitItem())
                assertEquals(ProfileUiState(isLoading = false, profile = profile), awaitItem())
                assertEquals(
                    ProfileUiState(isLoading = false, profile = profile, aboutFields = profile.aboutFields()),
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
        val displayNameField = aboutFields.find { it.type == AboutInputField.DisplayName }
        assertEquals("DisplayName should match mock profile", profile.displayName, displayNameField?.value)

        val aboutMeField = aboutFields.find { it.type == AboutInputField.AboutMe }
        assertEquals("AboutMe should match mock profile", profile.description, aboutMeField?.value)

        val pronounsField = aboutFields.find { it.type == AboutInputField.Pronouns }
        assertEquals("Pronouns should match mock profile", profile.pronouns, pronounsField?.value)

        val pronunciationField = aboutFields.find { it.type == AboutInputField.Pronunciation }
        assertEquals("Pronunciation should match mock profile", profile.pronunciation, pronunciationField?.value)

        val locationField = aboutFields.find { it.type == AboutInputField.Location }
        assertEquals("Location should match mock profile", profile.location, locationField?.value)

        val jobTitleField = aboutFields.find { it.type == AboutInputField.JobTitle }
        assertEquals("JobTitle should match mock profile", "Software Engineer", jobTitleField?.value)

        val companyField = aboutFields.find { it.type == AboutInputField.Company }
        assertEquals("Company should match mock profile", "Test Company", companyField?.value)

        val firstNameField = aboutFields.find { it.type == AboutInputField.FirstName }
        assertEquals("FirstName should match mock profile", profile.firstName.orEmpty(), firstNameField?.value)

        val lastNameField = aboutFields.find { it.type == AboutInputField.LastName }
        assertEquals("LastName should match mock profile", profile.lastName.orEmpty(), lastNameField?.value)
    }

    private fun profile() = Profile {
        hash = "mock-hash"
        displayName = "John Doe"
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
    }

    private fun initViewModel() = ProfileViewModel(profileService)
}
