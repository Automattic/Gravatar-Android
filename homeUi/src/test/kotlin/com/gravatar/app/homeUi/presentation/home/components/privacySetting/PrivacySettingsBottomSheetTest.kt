package com.gravatar.app.homeUi.presentation.home.components.privacySetting

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.app.usercomponent.domain.model.PrivacySettings
import org.junit.Test

class PrivacySettingsBottomSheetTest : RoborazziTest() {

    @Test
    fun privacySettingsAllEnabled() = screenshotTest {
        GravatarAppTheme {
            PrivacySettings(
                uiState = PrivacySettingUiState(
                    privacySettings = PrivacySettings(
                        analyticsEnabled = true,
                        crashReportingEnabled = true
                    )
                ),
                onEvent = { },
                onDismissRequest = { },
            )
        }
    }

    @Test
    fun privacySettingsAllDisabled() = screenshotTest {
        GravatarAppTheme {
            PrivacySettings(
                uiState = PrivacySettingUiState(
                    privacySettings = PrivacySettings(
                        analyticsEnabled = false,
                        crashReportingEnabled = false
                    )
                ),
                onEvent = { },
                onDismissRequest = { },
            )
        }
    }
}
