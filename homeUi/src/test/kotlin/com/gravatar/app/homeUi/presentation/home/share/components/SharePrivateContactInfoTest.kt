package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.presentation.home.share.PrivateContactState
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class SharePrivateContactInfoTest : RoborazziTest() {

    @Test
    fun sharePrivateContactInfo_bothSwitchesOn() = screenshotTest {
        GravatarAppTheme {
            SharePrivateContactInfo(
                privateContactState = PrivateContactState(
                    emailValue = "example@email.com",
                    isEmailShared = true,
                    phoneValue = "123-456-7890",
                    isPhoneShared = true
                ),
                onEmailValueChange = {},
                onEmailSwitchCheckedChange = {},
                onPhoneValueChange = {},
                onPhoneSwitchCheckedChange = {},
                onTitleClicked = {},
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
                privateContactState = PrivateContactState(
                    emailValue = "example@email.com",
                    isEmailShared = false,
                    phoneValue = "123-456-7890",
                    isPhoneShared = false
                ),
                onEmailValueChange = {},
                onEmailSwitchCheckedChange = {},
                onPhoneValueChange = {},
                onPhoneSwitchCheckedChange = {},
                onTitleClicked = {},
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
                privateContactState = PrivateContactState(
                    emailValue = "",
                    isEmailShared = true,
                    phoneValue = "",
                    isPhoneShared = true
                ),
                onEmailValueChange = {},
                onEmailSwitchCheckedChange = {},
                onPhoneValueChange = {},
                onPhoneSwitchCheckedChange = {},
                onTitleClicked = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
