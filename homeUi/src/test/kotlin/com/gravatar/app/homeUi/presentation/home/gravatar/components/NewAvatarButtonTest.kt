package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class NewAvatarButtonTest : RoborazziTest() {

    @Test
    fun newAvatarButton() = screenshotTest {
        GravatarAppTheme {
            NewAvatarButton(
                label = "Camera",
                contentDescription = "Camera",
                iconRes = R.drawable.ic_camera,
                modifier = Modifier.width(150.dp),
                onClick = { },
            )
        }
    }
}
