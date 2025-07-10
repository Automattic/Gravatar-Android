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
                ),
                onEvent = { },
            )
        }
    }

    @Test
    fun profileScreenWithNullProfile() {
        screenshotTest {
            ProfileScreen(
                uiState = ProfileUiState(
                    isLoading = false,
                    profile = null,
                ),
                onEvent = { },
            )
        }
    }

    @Test
    fun profileScreenWithNullProfileAndLoading() {
        screenshotTest {
            ProfileScreen(
                uiState = ProfileUiState(
                    isLoading = true,
                    profile = null,
                ),
                onEvent = { },
            )
        }
    }
}
