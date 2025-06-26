package com.gravatar.app.homeUi.presentation.home

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class HomeScreenTest : RoborazziTest() {

    @Test
    fun homeScreen() {
        screenshotTest {
            HomeScreen(
                onLoggedOut = { }
            )
        }
    }
}
