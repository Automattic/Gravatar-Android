package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    fun `when OnDeleteAccount event is received then deleteUserProfile is invoked`() = runTest {
        // When
        viewModel.onEvent(AboutAppDialogEvent.OnDeleteAccount)
        advanceUntilIdle()

        // Then
        coVerify { deleteUserProfile.invoke() }
    }
}
