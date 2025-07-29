package com.gravatar.app.homeUi.presentation.home.share.components

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class PrivateInformationDialogTest : RoborazziTest() {

    @Test
    fun privateInformationDialog() = screenshotTest {
        GravatarAppTheme {
            PrivateInformationDialogContent(
                onDismissRequest = {},
            )
        }
    }
}
