package com.gravatar.app.homeUi.presentation.home.components.privacySetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravatar.app.usercomponent.domain.model.PrivacySettings
import com.gravatar.app.usercomponent.domain.usecase.GetPrivacySettings
import com.gravatar.app.usercomponent.domain.usecase.UpdatePrivacySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PrivacySettingsViewModel(
    private val getPrivacySettings: GetPrivacySettings,
    private val updatePrivacySettings: UpdatePrivacySettings,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySettingUiState())
    val uiState: StateFlow<PrivacySettingUiState> = _uiState.asStateFlow()

    init {
        collectPrivacySettings()
    }

    fun onEvent(event: PrivacySettingsEvent) {
        when (event) {
            is PrivacySettingsEvent.OnAnalyticsEnabledChanged -> {
                val newSettings = _uiState.value.privacySettings.copy(analyticsEnabled = event.enabled)
                updateSettings(newSettings)
            }

            is PrivacySettingsEvent.OnCrashReportingEnabledChanged -> {
                val newSettings = _uiState.value.privacySettings.copy(crashReportingEnabled = event.enabled)
                updateSettings(newSettings)
            }
        }
    }

    private fun collectPrivacySettings() {
        getPrivacySettings()
            .onEach { settings ->
                _uiState.update { current ->
                    current.copy(privacySettings = settings)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateSettings(newSettings: PrivacySettings) {
        _uiState.update { it.copy(privacySettings = newSettings) }
        viewModelScope.launch {
            updatePrivacySettings(newSettings)
        }
    }
}
