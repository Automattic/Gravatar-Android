package com.gravatar.app.homeUi.presentation.home

import com.gravatar.app.homeUi.presentation.home.profile.ProfileScreen
import com.gravatar.app.homeUi.presentation.home.profile.ProfileUiState
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class HomeScreenTest : RoborazziTest() {

    @Test
    fun homeScreen() {
        screenshotTest {
            HomeScreen {
                ProfileScreen(
                    uiState = ProfileUiState(),
                    onEvent = {},
                )
            }
        }
    }
}
