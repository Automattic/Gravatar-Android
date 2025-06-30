package com.gravatar.app.homeUi.presentation.home.gravatar

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.restapi.models.Avatar
import org.junit.Test
import java.net.URI

class GravatarScreenTest : RoborazziTest() {

    @Test
    fun gravatarScreen() {
        screenshotTest {
            GravatarScreen(
                uiState = GravatarUiState(
                    isLoading = false,
                    avatars = List(10) {
                        Avatar {
                            imageUrl = URI.create("https://gravatar.com/avatar/test")
                            imageId = it.toString()
                            rating = Avatar.Rating.G
                            altText = "alt"
                            updatedDate = ""
                        }
                    }
                ),
                onEvent = { }
            )
        }
    }
}
