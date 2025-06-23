package com.gravatar.app.homeUi.presentation.login

import com.gravatar.app.homeUi.presentation.home.HomeScreen
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class HomeScreenTest : RoborazziTest() {

    @Test
    fun loginScreen_captureScreenshot() {
        screenshotTest {
            HomeScreen(
                onLoggedOut = { }
            )
        }
    }
}
