package com.gravatar.app.homeUi.presentation.home.profile.about

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import org.junit.Test

class AboutSectionTest : RoborazziTest() {

    @Test
    fun aboutSection_formEnabledFalse() = screenshotTest {
        MaterialTheme {
            AboutSection(
                aboutFields = setOf(
                    AboutEditorField(
                        type = AboutInputField.DisplayName,
                        value = "John Doe",
                        maxLines = 1,
                    ),
                    AboutEditorField(
                        type = AboutInputField.AboutMe,
                        value = "My description",
                        maxLines = 3,
                    ),
                    AboutEditorField(
                        type = AboutInputField.Pronunciation,
                        value = "John Doe",
                    ),
                    AboutEditorField(
                        type = AboutInputField.Pronouns,
                        value = "he/him",
                    ),
                    AboutEditorField(
                        type = AboutInputField.Location,
                        value = "San Francisco, CA",
                    ),
                    AboutEditorField(
                        type = AboutInputField.Company,
                        value = "Automattic",
                    ),
                    AboutEditorField(
                        type = AboutInputField.JobTitle,
                        value = "Software Engineer",
                    ),
                    AboutEditorField(
                        type = AboutInputField.FirstName,
                        value = "John",
                    ),
                    AboutEditorField(
                        type = AboutInputField.LastName,
                        value = "Doe",
                    ),
                ),
                formEnabled = false,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
