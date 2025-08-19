package com.gravatar.app.homeUi.presentation.home.gravatar

import android.net.Uri
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.restapi.models.Avatar
import org.junit.Test
import java.net.URI

class GravatarScreenTest : RoborazziTest() {

    @Test
    fun gravatarScreen() = screenshotTest {
        GravatarAppTheme {
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
                            selected = it == 0
                        }
                    },
                    selectedAvatarId = "0"
                ),
                onEvent = { },
                onTakePictureClicked = { },
                onPickMediaClicked = { },
            )
        }
    }

    @Test
    fun gravatarScreenWithAvatarsButNoneSelected() = screenshotTest {
        GravatarAppTheme {
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
                            selected = false
                        }
                    },
                    selectedAvatarId = null
                ),
                onEvent = { },
                onTakePictureClicked = { },
                onPickMediaClicked = { },
            )
        }
    }

    @Test
    fun gravatarScreenWithOnlyOneAvatarBeingUploaded() = screenshotTest {
        GravatarAppTheme {
            GravatarScreen(
                uiState = GravatarUiState(
                    isLoading = false,
                    avatars = emptyList(),
                    uploadingAvatar = Uri.EMPTY,
                    selectedAvatarId = null
                ),
                onEvent = { },
                onTakePictureClicked = { },
                onPickMediaClicked = { },
            )
        }
    }

    @Test
    fun gravatarScreenWithNullAvatars() = screenshotTest {
        GravatarAppTheme {
            GravatarScreen(
                uiState = GravatarUiState(
                    isLoading = false,
                    avatars = null,
                    selectedAvatarId = "0"
                ),
                onEvent = { },
                onTakePictureClicked = { },
                onPickMediaClicked = { },
            )
        }
    }

    @Test
    fun gravatarScreenWithEmptyAvatars() = screenshotTest {
        GravatarAppTheme {
            GravatarScreen(
                uiState = GravatarUiState(
                    isLoading = false,
                    avatars = emptyList(),
                    selectedAvatarId = "0"
                ),
                onEvent = { },
                onTakePictureClicked = { },
                onPickMediaClicked = { },
            )
        }
    }
}
