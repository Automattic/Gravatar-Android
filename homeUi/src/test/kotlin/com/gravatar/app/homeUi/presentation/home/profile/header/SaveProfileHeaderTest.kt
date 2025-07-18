package com.gravatar.app.homeUi.presentation.home.profile.header

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.ui.GravatarTheme
import org.junit.Test

class SaveProfileHeaderTest : RoborazziTest() {

    @Test
    fun profileHeader_unsavedState() = screenshotTest {
        GravatarTheme {
            SaveProfileHeader(
                saveState = SaveProfileHeaderState.UNSAVED,
                onSaveProfile = {},
                onCancelProfile = {},
            )
        }
    }

    @Test
    fun profileHeader_savingState() = screenshotTest {
        GravatarTheme {
            SaveProfileHeader(
                saveState = SaveProfileHeaderState.SAVING,
                onSaveProfile = {},
                onCancelProfile = {},
            )
        }
    }
}
