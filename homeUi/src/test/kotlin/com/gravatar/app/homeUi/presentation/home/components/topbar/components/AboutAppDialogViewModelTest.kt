package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import app.cash.turbine.test
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.AboutAppDialogEvent
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.AboutAppDialogState
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.AboutAppDialogViewModel
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AboutAppDialogViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private val deleteUserProfile: DeleteUserProfile = mockk()
    private lateinit var viewModel: AboutAppDialogViewModel

    @Before
    fun setup() {
        coEvery { deleteUserProfile.invoke() } returns Result.success(Unit)

        viewModel = AboutAppDialogViewModel(deleteUserProfile)
    }

    @Test
    fun `when OnShowDeleteConfirmation event is received then delete confirmation is shown`() = runTest {
        // When
        viewModel.onEvent(AboutAppDialogEvent.OnShowDeleteConfirmation)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = AboutAppDialogState(
                isDeleteConfirmationVisible = true,
                isLoading = false,
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `when OnHideDeleteConfirmation event is received then delete confirmation is hidden`() = runTest {
        // Given
        viewModel.onEvent(AboutAppDialogEvent.OnShowDeleteConfirmation)

        // When
        viewModel.onEvent(AboutAppDialogEvent.OnHideDeleteConfirmation)
        advanceUntilIdle()

        viewModel.uiState.test {
            val expectedState = AboutAppDialogState(
                isDeleteConfirmationVisible = false,
                isLoading = false,
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `when OnConfirmDeleteAccount event is received then deleteUserProfile is invoked and confirmation is hidden`() = runTest {
        // Given
        viewModel.onEvent(AboutAppDialogEvent.OnShowDeleteConfirmation)

        // When
        viewModel.onEvent(AboutAppDialogEvent.OnConfirmDeleteAccount)
        advanceUntilIdle()

        // Then
        coVerify { deleteUserProfile.invoke() }
        viewModel.uiState.test {
            val expectedState = AboutAppDialogState(
                isDeleteConfirmationVisible = false,
                isLoading = true,
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `when OnConfirmDeleteAccount event is received then isLoading is set to true`() = runTest {
        // Given
        viewModel.onEvent(AboutAppDialogEvent.OnShowDeleteConfirmation)

        // When
        viewModel.onEvent(AboutAppDialogEvent.OnConfirmDeleteAccount)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val expectedState = AboutAppDialogState(
                isDeleteConfirmationVisible = false,
                isLoading = true,
            )
            assertEquals(expectedState, awaitItem())
        }
    }

    @Test
    fun `when OnConfirmDeleteAccount event is received and deleteUserProfile fails then isLoading is set to false`() = runTest {
        // Given
        coEvery { deleteUserProfile.invoke() } returns Result.failure(Exception("Failed to delete profile"))
        viewModel = AboutAppDialogViewModel(deleteUserProfile)

        // When
        viewModel.onEvent(AboutAppDialogEvent.OnConfirmDeleteAccount)

        // Then
        viewModel.uiState.test {
            val expectedState = AboutAppDialogState(
                isDeleteConfirmationVisible = false,
                isLoading = false,
            )
            assertEquals(expectedState, awaitItem())
        }
    }
}
