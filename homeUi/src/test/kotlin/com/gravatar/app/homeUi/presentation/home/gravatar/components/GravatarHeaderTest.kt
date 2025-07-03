package com.gravatar.app.homeUi.presentation.home.gravatar.components

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.restapi.models.Avatar
import org.junit.Test
import java.net.URI

class GravatarHeaderTest : RoborazziTest() {

    private val avatar = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test")
        imageId = "id"
        rating = Avatar.Rating.G
        altText = "alt"
        updatedDate = ""
    }

    @Test
    fun gravatarHeaderWithAvatar() = screenshotTest {
        GravatarHeader(
            avatar = avatar
        )
    }
}
