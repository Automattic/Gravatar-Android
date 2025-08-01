package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class AboutAppDialogTest : RoborazziTest() {

    @Test
    fun aboutAppDialogVisible() = screenshotTest {
        GravatarAppTheme {
            AboutAppDialogContent(
                appVersion = "1.0.0",
                onDone = {},
                onEvent = { _ -> }
            )
        }
    }
}
