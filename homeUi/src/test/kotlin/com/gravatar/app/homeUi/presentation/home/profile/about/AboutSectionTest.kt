package com.gravatar.app.homeUi.presentation.home.profile.about

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class AboutSectionTest : RoborazziTest() {

    @Test
    fun aboutSection_nameSection_formEnabledFalse() = screenshotTest {
        GravatarAppTheme {
            AboutSection(
                aboutFields = setOf(
                    AboutEditorField(
                        type = AboutInputField.FIRST_NAME,
                        value = "John",
                        edited = true,
                    ),
                    AboutEditorField(
                        type = AboutInputField.LAST_NAME,
                        value = "Doe",
                    ),
                    AboutEditorField(
                        type = AboutInputField.DISPLAY_NAME,
                        value = "John Doe",
                        maxLines = 1,
                    ),
                    AboutEditorField(
                        type = AboutInputField.PRONUNCIATION,
                        value = "John Doe",
                    ),
                    AboutEditorField(
                        type = AboutInputField.PRONOUNS,
                        value = "he/him",
                    ),
                ),
                formEnabled = false,
                onValueChange = { },
                onFieldFocused = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Test
    fun aboutSection_professionalSection_formEnabledFalse() = screenshotTest {
        GravatarAppTheme {
            AboutSection(
                aboutFields = setOf(
                    AboutEditorField(
                        type = AboutInputField.JOB_TITLE,
                        value = "Software Engineer",
                    ),
                    AboutEditorField(
                        type = AboutInputField.COMPANY,
                        value = "Automattic",
                    ),
                ),
                formEnabled = false,
                onValueChange = { },
                onFieldFocused = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Test
    fun aboutSection_aboutSection_formEnabledFalse() = screenshotTest {
        GravatarAppTheme {
            AboutSection(
                aboutFields = setOf(
                    AboutEditorField(
                        type = AboutInputField.ABOUT_ME,
                        value = "My description",
                        maxLines = 3,
                    ),
                    AboutEditorField(
                        type = AboutInputField.LOCATION,
                        value = "San Francisco, CA",
                    ),
                ),
                formEnabled = false,
                onValueChange = { },
                onFieldFocused = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    @Test
    fun aboutSection_mixSections_formEnabledFalse() = screenshotTest {
        GravatarAppTheme {
            AboutSection(
                aboutFields = setOf(
                    AboutEditorField(
                        type = AboutInputField.FIRST_NAME,
                        value = "John",
                    ),
                    AboutEditorField(
                        type = AboutInputField.JOB_TITLE,
                        value = "Software Engineer",
                    ),
                    AboutEditorField(
                        type = AboutInputField.LOCATION,
                        value = "San Francisco, CA",
                    ),
                ),
                formEnabled = false,
                onValueChange = { },
                onFieldFocused = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
