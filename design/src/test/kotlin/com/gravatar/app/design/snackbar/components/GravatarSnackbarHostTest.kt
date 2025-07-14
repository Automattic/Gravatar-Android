package com.gravatar.app.design.snackbar.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.gravatar.app.design.components.snackbar.GravatarSnackbarHost
import com.gravatar.app.design.components.snackbar.SnackbarType
import com.gravatar.app.design.components.snackbar.showGravatarSnackbar
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class GravatarSnackbarHostTest : RoborazziTest() {

    @Test
    fun gravatarSnackbarHost_info() = screenshotTest {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackbarHostState.showGravatarSnackbar(
                message = "This is an info snackbar",
                actionLabel = "Action",
                snackbarType = SnackbarType.Info
            )
        }

        GravatarSnackbarHost(hostState = snackbarHostState)
    }

    @Test
    fun gravatarSnackbarHost_error() = screenshotTest {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackbarHostState.showGravatarSnackbar(
                message = "This is an error snackbar",
                actionLabel = "Action",
                snackbarType = SnackbarType.Error
            )
        }

        GravatarSnackbarHost(hostState = snackbarHostState)
    }

    @Test
    fun gravatarSnackbarHost_withoutAction() = screenshotTest {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackbarHostState.showGravatarSnackbar(
                message = "This is a snackbar without action",
                snackbarType = SnackbarType.Info
            )
        }

        GravatarSnackbarHost(hostState = snackbarHostState)
    }

    @Test
    fun gravatarSnackbarHost_withDismissAction() = screenshotTest {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            snackbarHostState.showGravatarSnackbar(
                message = "This is a snackbar with dismiss action",
                withDismissAction = true,
                snackbarType = SnackbarType.Info
            )
        }

        GravatarSnackbarHost(hostState = snackbarHostState)
    }
}
