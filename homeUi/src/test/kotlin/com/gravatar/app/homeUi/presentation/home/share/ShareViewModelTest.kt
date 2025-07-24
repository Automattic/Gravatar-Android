package com.gravatar.app.homeUi.presentation.home.share

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
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
    private val userRepository = mockk<UserRepository>()

    private lateinit var viewModel: ShareViewModel

    private val avatarUrlFlow: MutableSharedFlow<URL?> = MutableSharedFlow()
    private val profileFlow: MutableSharedFlow<Profile?> = MutableSharedFlow()

    @Before
    fun setup() {
        every { userRepository.getProfile() } returns profileFlow
        viewModel = ShareViewModel(userRepository, getAvatarUrl)
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
            assertEquals(newEmailValue, awaitItem().privateContactInfo.emailValue)
        }
    }

    @Test
    fun `when OnEmailSharingChanged event is triggered then isEmailShared is updated`() = runTest {
        // Given
        val isShared = true

        // When
        viewModel.onEvent(ShareEvent.OnEmailSharingChanged(isShared))

        // Then
        viewModel.uiState.test {
            assertEquals(isShared, awaitItem().privateContactInfo.isEmailShared)
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
            assertEquals(newPhoneValue, awaitItem().privateContactInfo.phoneValue)
        }
    }

    @Test
    fun `when OnPhoneSharingChanged event is triggered then isPhoneShared is updated`() = runTest {
        // Given
        val isShared = true

        // When
        viewModel.onEvent(ShareEvent.OnPhoneSharingChanged(isShared))

        // Then
        viewModel.uiState.test {
            assertEquals(isShared, awaitItem().privateContactInfo.isPhoneShared)
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
