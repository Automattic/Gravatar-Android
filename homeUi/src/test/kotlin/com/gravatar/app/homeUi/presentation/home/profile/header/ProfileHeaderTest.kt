package com.gravatar.app.homeUi.presentation.home.profile.header

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
            saveState = ProfileHeaderSaveState.SAVED,
            onSaveProfile = {},
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
            saveState = ProfileHeaderSaveState.SAVED,
            onSaveProfile = {},
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
            saveState = ProfileHeaderSaveState.SAVED,
            onSaveProfile = {},
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
            saveState = ProfileHeaderSaveState.SAVED,
            onSaveProfile = {},
        )
    }

    @Test
    fun profileHeader_longTextWithEllipsis() = screenshotTest {
        ProfileHeader(
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe with a very long name that should trigger ellipsis in the UI",
                jobTitle = "Senior Software Engineer with a very long title",
                company = "Automattic Inc. - A very long company name that should also trigger ellipsis"
            ),
            saveState = ProfileHeaderSaveState.SAVED,
            onSaveProfile = {},
        )
    }
}
