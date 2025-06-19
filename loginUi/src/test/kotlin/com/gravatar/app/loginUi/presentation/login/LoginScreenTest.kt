package com.gravatar.app.loginUi.presentation.login

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class LoginScreenTest : RoborazziTest() {

    @Test
    fun loginScreen_captureScreenshot() {
        screenshotTest {
            LoginScreen(
                onLoggedIn = { }
            )
        }
    }
}
