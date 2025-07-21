package com.gravatar.app.homeUi.presentation.home.profile.header

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class SaveProfileHeaderTest : RoborazziTest() {

    @Test
    fun profileHeader_unsavedState() = screenshotTest {
        GravatarAppTheme {
            SaveProfileHeader(
                saveState = SaveProfileHeaderState.UNSAVED,
                onSaveProfile = {},
                onCancelProfile = {},
            )
        }
    }

    @Test
    fun profileHeader_savingState() = screenshotTest {
        GravatarAppTheme {
            SaveProfileHeader(
                saveState = SaveProfileHeaderState.SAVING,
                onSaveProfile = {},
                onCancelProfile = {},
            )
        }
    }
}
