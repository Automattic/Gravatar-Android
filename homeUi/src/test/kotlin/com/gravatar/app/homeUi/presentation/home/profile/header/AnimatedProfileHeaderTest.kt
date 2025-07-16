package com.gravatar.app.homeUi.presentation.home.profile.header

import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.extensions.defaultProfile
import org.junit.Test

class AnimatedProfileHeaderTest : RoborazziTest() {

    @Test
    fun animatedProfileHeader_whenExpanded_showsCorrectLayout() {
        screenshotTest {
            AnimatedProfileHeader(
                profile = defaultProfile(
                    hash = "",
                    displayName = "John Doe",
                    jobTitle = "Software Engineer",
                    company = "Automattic"
                ),
                avatarUrl = "https://gravatar.com/avatar/test",
                saveState = ProfileHeaderSaveState.SAVED,
                onSaveProfile = {},
                headerState = AnimatedProfileHeaderState.EXPANDED
            )
        }
    }

    @Test
    fun animatedProfileHeader_whenCollapsed_showsCorrectLayout() {
        screenshotTest {
            AnimatedProfileHeader(
                profile = defaultProfile(
                    hash = "",
                    displayName = "John Doe",
                    jobTitle = "Software Engineer",
                    company = "Automattic"
                ),
                avatarUrl = "https://gravatar.com/avatar/test",
                saveState = ProfileHeaderSaveState.SAVED,
                onSaveProfile = {},
                headerState = AnimatedProfileHeaderState.COLLAPSED
            )
        }
    }

    @Test
    fun animatedProfileHeader_duringTransition_animatesCorrectly() {
        screenshotTest {
            AnimatedProfileHeader(
                profile = defaultProfile(
                    hash = "",
                    displayName = "John Doe",
                    jobTitle = "Software Engineer",
                    company = "Automattic"
                ),
                avatarUrl = "https://gravatar.com/avatar/test",
                saveState = ProfileHeaderSaveState.SAVED,
                onSaveProfile = {},
                headerState = AnimatedProfileHeaderState(0.5f)
            )
        }
    }
}
