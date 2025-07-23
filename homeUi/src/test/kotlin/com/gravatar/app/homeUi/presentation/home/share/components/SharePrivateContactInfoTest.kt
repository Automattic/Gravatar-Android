package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class SharePrivateContactInfoTest : RoborazziTest() {

    @Test
    fun sharePrivateContactInfo_bothSwitchesOn() = screenshotTest {
        GravatarAppTheme {
            SharePrivateContactInfo(
                emailValue = "example@email.com",
                onEmailValueChange = {},
                emailSwitchChecked = true,
                onEmailSwitchCheckedChange = {},
                phoneValue = "123-456-7890",
                onPhoneValueChange = {},
                phoneSwitchChecked = true,
                onPhoneSwitchCheckedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    @Test
    fun sharePrivateContactInfo_bothSwitchesOff() = screenshotTest {
        GravatarAppTheme {
            SharePrivateContactInfo(
                emailValue = "example@email.com",
                onEmailValueChange = {},
                emailSwitchChecked = false,
                onEmailSwitchCheckedChange = {},
                phoneValue = "123-456-7890",
                onPhoneValueChange = {},
                phoneSwitchChecked = false,
                onPhoneSwitchCheckedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    @Test
    fun sharePrivateContactInfo_emptyValues() = screenshotTest {
        GravatarAppTheme {
            SharePrivateContactInfo(
                emailValue = "",
                onEmailValueChange = {},
                emailSwitchChecked = true,
                onEmailSwitchCheckedChange = {},
                phoneValue = "",
                onPhoneValueChange = {},
                phoneSwitchChecked = true,
                onPhoneSwitchCheckedChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
