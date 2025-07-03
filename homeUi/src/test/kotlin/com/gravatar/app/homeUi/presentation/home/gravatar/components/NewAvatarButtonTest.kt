package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import com.gravatar.app.homeUi.R
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class NewAvatarButtonTest : RoborazziTest() {

    @Test
    fun newAvatarButton() = screenshotTest {
        NewAvatarButton(
            label = "Camera",
            contentDescription = "Camera",
            iconRes = R.drawable.ic_camera,
            modifier = Modifier.wrapContentSize(),
            onClick = { },
        )
    }
}
