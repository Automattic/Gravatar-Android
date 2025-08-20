package com.gravatar.app.homeUi.presentation.home.components.privacySetting

import app.cash.turbine.test
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.PrivacySettings
import com.gravatar.app.usercomponent.domain.usecase.GetPrivacySettings
import com.gravatar.app.usercomponent.domain.usecase.UpdatePrivacySettings
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrivacySettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var viewModel: PrivacySettingsViewModel
    private lateinit var privacySettingsFlow: MutableSharedFlow<PrivacySettings>

    private val getPrivacySettings: GetPrivacySettings = object : GetPrivacySettings {
        override fun invoke() = privacySettingsFlow
    }
    private val updatePrivacySettings: UpdatePrivacySettings = mockk(relaxed = true)

    @Before
    fun setup() {
        privacySettingsFlow = MutableSharedFlow()
        viewModel = PrivacySettingsViewModel(
            getPrivacySettings = getPrivacySettings,
            updatePrivacySettings = updatePrivacySettings,
        )
    }

    @Test
    fun `init should collect privacy settings and update ui state`() = runTest {
        // Given
        val emittedSettings = PrivacySettings(analyticsEnabled = false, crashReportingEnabled = true)

        // When
        privacySettingsFlow.emit(emittedSettings)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(
                PrivacySettingUiState(privacySettings = emittedSettings),
                awaitItem()
            )
        }
    }

    @Test
    fun `onEvent OnAnalyticsEnabledChanged should update state and call update use case`() = runTest {
        // Given: initial state is default (true, true)

        // When
        viewModel.onEvent(PrivacySettingsEvent.OnAnalyticsEnabledChanged(false))
        advanceUntilIdle()

        // Then: state updated immediately
        viewModel.uiState.test {
            assertEquals(
                PrivacySettingUiState(
                    privacySettings = PrivacySettings(analyticsEnabled = false, crashReportingEnabled = true)
                ),
                awaitItem()
            )
        }
        // And use case invoked with new settings
        coVerify {
            updatePrivacySettings.invoke(
                PrivacySettings(analyticsEnabled = false, crashReportingEnabled = true)
            )
        }
    }

    @Test
    fun `onEvent OnCrashReportingEnabledChanged should update state and call update use case`() = runTest {
        // Given: initial state is default (true, true)

        // When
        viewModel.onEvent(PrivacySettingsEvent.OnCrashReportingEnabledChanged(false))
        advanceUntilIdle()

        // Then: state updated immediately
        viewModel.uiState.test {
            assertEquals(
                PrivacySettingUiState(
                    privacySettings = PrivacySettings(analyticsEnabled = true, crashReportingEnabled = false)
                ),
                awaitItem()
            )
        }
        // And use case invoked with new settings
        coVerify {
            updatePrivacySettings.invoke(
                PrivacySettings(analyticsEnabled = true, crashReportingEnabled = false)
            )
        }
    }
}
