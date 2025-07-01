package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.ui.Modifier
import com.gravatar.app.homeUi.presentation.home.gravatar.AvatarUi
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.restapi.models.Avatar
import org.junit.Test
import java.net.URI

class SelectableAvatarTest : RoborazziTest() {

    private val avatar = Avatar {
        imageUrl = URI.create("https://gravatar.com/avatar/test")
        imageId = "id"
        rating = Avatar.Rating.G
        altText = "alt"
        updatedDate = ""
    }

    @Test
    fun selectableAvatarSelected() = screenshotTest {
        SelectableAvatar(
            avatar = AvatarUi.Uploaded(
                isSelected = true,
                isLoading = false,
                avatar = avatar
            ),
            size = avatarSize,
            onAvatarOptionClicked = { _, _ -> },
            modifier = Modifier,
        )
    }

    @Test
    fun selectableAvatarLoading() = screenshotTest {
        SelectableAvatar(
            avatar = AvatarUi.Uploaded(
                isSelected = true,
                isLoading = true,
                avatar = avatar
            ),
            size = avatarSize,
            onAvatarOptionClicked = { _, _ -> },
            modifier = Modifier,
        )
    }

    @Test
    fun selectableAvatarLoaded() = screenshotTest {
        SelectableAvatar(
            avatar = AvatarUi.Uploaded(
                isSelected = false,
                isLoading = false,
                avatar = avatar
            ),
            size = avatarSize,
            onAvatarOptionClicked = { _, _ -> },
            modifier = Modifier,
        )
    }
}
