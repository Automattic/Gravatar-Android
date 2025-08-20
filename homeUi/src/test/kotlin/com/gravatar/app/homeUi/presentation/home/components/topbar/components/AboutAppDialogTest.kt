package com.gravatar.app.homeUi.presentation.home.components.topbar.components

import androidx.compose.material3.ExperimentalMaterial3Api
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.AboutAppDialogContent
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.about.DeleteConfirmationBottomSheet
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class AboutAppDialogTest : RoborazziTest() {

    @Test
    fun aboutAppDialogVisible() = screenshotTest {
        GravatarAppTheme {
            AboutAppDialogContent(
                appVersion = "1.0.0",
                onDone = {},
                onEvent = { _ -> },
                onPrivacySettingsClicked = {}
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun deleteConfirmationBottomSheetVisible() = screenshotTest {
        GravatarAppTheme {
            DeleteConfirmationBottomSheet(
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}
