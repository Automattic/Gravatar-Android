package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class SharePublicRowTest : RoborazziTest() {

    @Test
    fun sharePublicRowSingleLine() = screenshotTest {
        GravatarAppTheme {
            SharePublicRow(
                label = "Label",
                value = "Value",
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.fillMaxWidth(),
                singleLineValue = true
            )
        }
    }

    @Test
    fun sharePublicRowMultiLine() = screenshotTest {
        GravatarAppTheme {
            SharePublicRow(
                label = "Label",
                value = "This is a very long public value that should wrap to multiple lines.",
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.fillMaxWidth(),
                singleLineValue = false
            )
        }
    }
}
