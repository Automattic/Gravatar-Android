package com.gravatar.app.homeUi.presentation.home

import com.gravatar.app.homeUi.presentation.home.profile.ProfileScreen
import com.gravatar.app.homeUi.presentation.home.profile.ProfileUiState
import com.gravatar.app.networkmonitor.NetworkState
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class HomeScreenTest : RoborazziTest() {

    @Test
    fun homeScreen() {
        screenshotTest {
            HomeScreen(
                uiState = HomeUiState(),
            ) { navController, snackbarHostState ->
                ProfileScreen(
                    uiState = ProfileUiState(),
                    onEvent = {},
                )
            }
        }
    }

    @Test
    fun homeScreenNoInternet() {
        screenshotTest {
            HomeScreen(
                uiState = HomeUiState(networkState = NetworkState.DISCONNECTED),
            ) { navController, snackbarHostState ->
                ProfileScreen(
                    uiState = ProfileUiState(),
                    onEvent = {},
                )
            }
        }
    }
}
