package com.gravatar.app.homeUi.presentation.home.gravatar.components

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class GravatarHeaderTest : RoborazziTest() {

    @Test
    fun gravatarHeaderFullyExpanded() = screenshotTest {
        GravatarHeader(
            avatarUrl = "https://gravatar.com/avatar/test",
            progress = 1.0f
        )
    }

    @Test
    fun gravatarHeaderFullyCollapsed() = screenshotTest {
        GravatarHeader(
            avatarUrl = "https://gravatar.com/avatar/test",
            progress = 0.0f
        )
    }

    @Test
    fun gravatarHeaderPartiallyExpanded() = screenshotTest {
        GravatarHeader(
            avatarUrl = "https://gravatar.com/avatar/test",
            progress = 0.5f
        )
    }
}
