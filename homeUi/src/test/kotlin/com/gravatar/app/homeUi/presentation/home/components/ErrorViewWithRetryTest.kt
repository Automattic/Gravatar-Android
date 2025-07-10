package com.gravatar.app.homeUi.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class ErrorViewWithRetryTest : RoborazziTest() {

    @Test
    fun errorViewWithRetry() {
        screenshotTest {
            ErrorViewWithRetry(
                errorTitle = "Network Error",
                errorMessage = "An unexpected error occurred. Please try again later.",
                onRetryClicked = {},
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
