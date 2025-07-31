package com.gravatar.app.homeUi.presentation.home.share.components

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class ExpandedQrCodeTest : RoborazziTest() {

    @Test
    fun expandedQrCode() = screenshotTest {
        GravatarAppTheme {
            ExpandedQrCode(
                vCardQrCodeData = "BEGIN:VCARD\nVERSION:3.0\nFN:Test User\nEND:VCARD",
                avatarUrl = "https://gravatar.com/avatar/test",
                onDismissRequest = {}
            )
        }
    }
}
