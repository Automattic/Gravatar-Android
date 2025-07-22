package com.gravatar.app.homeUi.presentation.home.components.topbar

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.net.URI

@OptIn(ExperimentalCoroutinesApi::class)
class TopBarPickerPopupViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val userRepository: UserRepository = mockk()
    private val logout: Logout = mockk()
    private lateinit var viewModel: TopBarPickerPopupViewModel

    private val profileFlow: MutableSharedFlow<Profile?> = MutableSharedFlow()

    @Test
    fun `onEvent OnLogoutSelected should invoke logout usecase`() = runTest {
        // Given
        coEvery { logout.invoke() } returns Unit
        initViewModel()

        // When
        viewModel.onEvent(TopBarPickerPopupEvent.OnLogoutSelected)
        advanceUntilIdle()

        // Then
        coVerify { logout.invoke() }
    }

    @Test
    fun `onEvent OnProfileLinkClicked should emit OpenExternalUrl action with correct URL`() = runTest {
        // Given
        initViewModel()
        advanceUntilIdle()

        val testProfile = createProfile()
        profileFlow.emit(testProfile)

        // When
        viewModel.onEvent(TopBarPickerPopupEvent.OnProfileLinkClicked)

        // Then
        viewModel.actions.test {
            assertEquals(TopBarPickerPopupAction.OpenExternalUrl(testProfile.profileUrl.toString()), awaitItem())
        }
    }

    @Test
    fun `onEvent OnGravatarLinkClicked should emit OpenExternalUrl action with Gravatar URL`() = runTest {
        // Given
        initViewModel()
        advanceUntilIdle()

        // When
        viewModel.onEvent(TopBarPickerPopupEvent.OnGravatarLinkClicked)

        // Then
        viewModel.actions.test {
            assertEquals(TopBarPickerPopupAction.OpenExternalUrl("https://www.gravatar.com"), awaitItem())
        }
    }

    @Test
    fun `onEvent OnShareProfileClicked should emit ShareProfileUrl action with correct URL`() = runTest {
        // Given
        initViewModel()
        advanceUntilIdle()

        val testProfile = createProfile()
        profileFlow.emit(testProfile)

        // When
        viewModel.onEvent(TopBarPickerPopupEvent.OnShareProfileClicked)

        // Then
        viewModel.actions.test {
            assertEquals(TopBarPickerPopupAction.ShareProfileUrl(testProfile.profileUrl.toString()), awaitItem())
        }
    }

    private fun initViewModel() {
        every { userRepository.getProfile() } returns profileFlow

        viewModel = TopBarPickerPopupViewModel(
            userRepository = userRepository,
            logout = logout,
        )
    }

    private fun createProfile(): Profile = Profile {
        hash = "test-hash"
        displayName = "Test User"
        profileUrl = URI.create("https://gravatar.com/test-hash")
        avatarUrl = URI.create("https://gravatar.com/avatar/test-hash")
        avatarAltText = "Avatar for Test User"
        description = "Test description"
        pronouns = "They/Them"
        pronunciation = "Test pronunciation"
        location = "Test location"
        jobTitle = "Test job title"
        company = "Test company"
        firstName = "Test"
        lastName = "User"
        verifiedAccounts = emptyList()
        contactInfo = ProfileContactInfo {
            cellPhone = "123-456-7890"
            email = "test@example.com"
        }
    }
}
