package com.gravatar.app.homeUi.presentation.home.gravatar.components

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class GravatarHeaderTest : RoborazziTest() {

    @Test
    fun gravatarHeaderWithAvatar() = screenshotTest {
        GravatarHeader(
            avatarUrl = "https://gravatar.com/avatar/test"
        )
    }
}
