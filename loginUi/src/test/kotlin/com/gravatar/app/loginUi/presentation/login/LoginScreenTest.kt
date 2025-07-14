package com.gravatar.app.loginUi.presentation.login

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test
import org.robolectric.annotation.Config

class LoginScreenTest : RoborazziTest() {

    @Test
    fun loginScreen() = screenshotTest {
        GravatarAppTheme {
            LoginScreen(
                uiState = LoginUiState(),
                onEvent = { }
            )
        }
    }

    @Test
    fun loginScreenAuthorizationDenied() = screenshotTest {
        GravatarAppTheme {
            LoginScreen(
                uiState = LoginUiState(
                    isLoading = false,
                    error = LoginError.AuthorizationDenied,
                ),
                onEvent = { }
            )
        }
    }

    @Test
    fun loginScreenProfileLoadFailed() = screenshotTest {
        GravatarAppTheme {
            LoginScreen(
                uiState = LoginUiState(
                    isLoading = false,
                    error = LoginError.ProfileLoadFailure(
                        reason = LoginError.ProfileLoadFailure.Reason.GENERIC_ERROR
                    ),
                ),
                onEvent = { }
            )
        }
    }

    @Test
    @Config(qualifiers = "+land")
    fun loginScreenLandscape() = screenshotTest {
        GravatarAppTheme {
            LoginScreen(
                uiState = LoginUiState(),
                onEvent = { }
            )
        }
    }

    @Test
    fun loginScreenDarkTheme() = screenshotTest {
        GravatarAppTheme(darkTheme = true) {
            LoginScreen(
                uiState = LoginUiState(),
                onEvent = { }
            )
        }
    }

    @Test
    fun loginScreenLoading() = screenshotTest {
        GravatarAppTheme {
            LoginScreen(
                uiState = LoginUiState(
                    isLoading = true,
                ),
                onEvent = { }
            )
        }
    }
}
