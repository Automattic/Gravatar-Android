package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.extensions.defaultProfile
import org.junit.Test

class ProfileHeaderTest : RoborazziTest() {

    @Test
    fun profileHeader_bothJobTitleAndCompany() = screenshotTest {
        ProfileHeader(
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "Software Engineer",
                company = "Automattic"
            ),
            modifier = Modifier.padding(16.dp),
        )
    }

    @Test
    fun profileHeader_onlyJobTitle() = screenshotTest {
        ProfileHeader(
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "Software Engineer",
                company = ""
            ),
            modifier = Modifier.padding(16.dp),
        )
    }

    @Test
    fun profileHeader_onlyCompany() = screenshotTest {
        ProfileHeader(
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "",
                company = "Automattic"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }

    @Test
    fun profileHeader_neitherJobTitleNorCompany() = screenshotTest {
        ProfileHeader(
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "",
                company = ""
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
