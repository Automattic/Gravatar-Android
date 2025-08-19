package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.testUtils.roborazzi.RoborazziTest
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import org.junit.Test
import java.net.URI

class SharePublicContactInfoTest : RoborazziTest() {

    val verifiedServiceUrl = "https://example.com/johndoe"

    val fullProfile = Profile {
        firstName = "John"
        lastName = "Doe"
        displayName = "Johny"
        hash = "1234567890abcdef1234567890abcdef"
        location = "New York, USA"
        jobTitle = "Software Engineer"
        company = "Acme Inc."
        description = "A passionate software engineer with a love for coding and technology."
        verifiedAccounts = emptyList()
        profileUrl = URI.create("https://johndoe.com")
        avatarUrl = URI.create("https://www.gravatar.com/avatar/123")
        avatarAltText = "John Doe's Gravatar"
        pronouns = "he/him"
        pronunciation = "John Doe"
        verifiedAccounts = listOf(
            VerifiedAccount {
                serviceIcon = URI.create("https://example.com/icon.png")
                serviceLabel = "Example Service"
                serviceType = "example"
                url = URI.create(verifiedServiceUrl)
                isHidden = false
            }
        )
    }

    @Test
    fun sharePublicContactInfoAllFields() = screenshotTest {
        GravatarAppTheme {
            SharePublicContactInfo(
                profile = fullProfile,
                userSharePreferences = UserSharePreferences.Default,
                onUserPreferenceChanged = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Test
    fun sharePublicContactInfoAllFieldsNonShared() = screenshotTest {
        GravatarAppTheme {
            SharePublicContactInfo(
                profile = fullProfile,
                userSharePreferences = UserSharePreferences(
                    name = false,
                    location = false,
                    title = false,
                    organization = false,
                    description = false,
                    profileUrl = false,
                    privateEmail = false,
                    privatePhone = false,
                    verifiedAccounts = mapOf(verifiedServiceUrl to false)
                ),
                onUserPreferenceChanged = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Test
    fun sharePublicContactInfoOnlyProfileUrl() = screenshotTest {
        GravatarAppTheme {
            SharePublicContactInfo(
                profile = defaultProfile(hash = "hash"),
                userSharePreferences = UserSharePreferences.Default,
                onUserPreferenceChanged = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
