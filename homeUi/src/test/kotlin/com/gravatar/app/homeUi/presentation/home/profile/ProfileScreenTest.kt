package com.gravatar.app.homeUi.presentation.home.profile

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.extensions.defaultProfile
import org.junit.Test

class ProfileScreenTest : RoborazziTest() {

    @Test
    fun profileScreen() {
        screenshotTest {
            ProfileScreen(
                uiState = ProfileUiState(
                    isLoading = false,
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    )
                )
            )
        }
    }
}
