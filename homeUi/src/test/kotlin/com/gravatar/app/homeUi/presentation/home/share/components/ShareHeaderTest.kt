package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class ShareHeaderTest : RoborazziTest() {

    @Test
    fun shareHeader() = screenshotTest {
        GravatarAppTheme {
            ShareHeader(
                avatarUrl = "url",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
