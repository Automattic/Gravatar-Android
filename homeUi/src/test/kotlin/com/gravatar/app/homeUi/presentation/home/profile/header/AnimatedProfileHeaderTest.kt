package com.gravatar.app.homeUi.presentation.home.profile.header

import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.extensions.defaultProfile
import org.junit.Test

class AnimatedProfileHeaderTest : RoborazziTest() {

    @Test
    fun animatedProfileHeader_whenExpanded_showsCorrectLayout() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState.EXPANDED,
                    onProfileLinkClicked = {}
                )
            }
        }
    }

    @Test
    fun animatedProfileHeader_withLocation_whenExpanded_showsCorrectLayout() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic",
                        location = "San Francisco, CA"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState.EXPANDED,
                    onProfileLinkClicked = {}
                )
            }
        }
    }

    @Test
    fun animatedProfileHeader_whenCollapsed_showsCorrectLayout() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState.COLLAPSED,
                    onProfileLinkClicked = {},
                )
            }
        }
    }

    @Test
    fun animatedProfileHeader_duringTransition_animatesCorrectly() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState(0.5f, AnimatedProfileHeaderSavingState.SAVED),
                    onProfileLinkClicked = {},
                )
            }
        }
    }

    @Test
    fun animatedProfileHeader_whenUnsaved_showsCorrectLayout() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState(1.0f, AnimatedProfileHeaderSavingState.UNSAVED),
                    onProfileLinkClicked = {},
                )
            }
        }
    }

    @Test
    fun animatedProfileHeader_whenSaving_showsCorrectLayout() {
        screenshotTest {
            GravatarAppTheme {
                AnimatedProfileHeader(
                    profile = defaultProfile(
                        hash = "",
                        displayName = "John Doe",
                        jobTitle = "Software Engineer",
                        company = "Automattic"
                    ),
                    avatarUrl = "https://gravatar.com/avatar/test",
                    onSaveProfile = {},
                    onCancelProfile = {},
                    headerState = AnimatedProfileHeaderState(1.0f, AnimatedProfileHeaderSavingState.SAVING),
                    onProfileLinkClicked = {},
                )
            }
        }
    }
}
