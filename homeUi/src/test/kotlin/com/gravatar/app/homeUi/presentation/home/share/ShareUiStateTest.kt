package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.URI

class ShareUiStateTest {

    @Test
    fun `when all preferences are enabled then all fields are included in vCard`() {
        // Given
        val profile = createTestProfile()
        val privateContactInfo = PrivateContactInfo(
            privateEmail = "private@example.com",
            privatePhone = "987-654-3210"
        )
        val userSharePreferences = UserSharePreferences.Default // All preferences are true by default

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            privateContactInfo = privateContactInfo,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertEquals("Test", firstName)
            assertEquals("User", lastName)
            assertEquals("Test User", nickname)
            assertEquals("Test Company", organization)
            assertEquals("Software Engineer", title)
            assertEquals("https://www.gravatar.com/test-hash", profileUrl)
            assertEquals("Test description", note)
            assertEquals("private@example.com", email)
            assertEquals("987-654-3210", phoneNumber)
        }
    }

    @Test
    fun `when all preferences are disabled then no fields are included in vCard`() {
        // Given
        val profile = createTestProfile()
        val privateContactInfo = PrivateContactInfo(
            privateEmail = "private@example.com",
            privatePhone = "987-654-3210"
        )
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = false,
            title = false,
            organization = false,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            privateContactInfo = privateContactInfo,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertNull(organization)
            assertNull(title)
            assertNull(profileUrl)
            assertNull(note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only name preference is enabled then only name fields are included in vCard`() {
        // Given
        val profile = createTestProfile()
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = true,
            location = false,
            title = false,
            organization = false,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertEquals("Test", firstName)
            assertEquals("User", lastName)
            assertNull(nickname)
            assertNull(organization)
            assertNull(title)
            assertNull(profileUrl)
            assertNull(note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only description preference is enabled then only description fields are included in vCard`() {
        // Given
        val profile = createTestProfile()
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = false,
            title = false,
            organization = false,
            description = true,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertEquals("Test User", nickname)
            assertNull(organization)
            assertNull(title)
            assertNull(profileUrl)
            assertEquals("Test description", note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only organization preference is enabled then only organization field is included in vCard`() {
        // Given
        val profile = createTestProfile()
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = false,
            title = false,
            organization = true,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertEquals("Test Company", organization)
            assertNull(title)
            assertNull(profileUrl)
            assertNull(note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only title preference is enabled then only title field is included in vCard`() {
        // Given
        val profile = createTestProfile()
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = false,
            title = true,
            organization = false,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertNull(organization)
            assertEquals("Software Engineer", title)
            assertNull(profileUrl)
            assertNull(note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only profileUrl preference is enabled then only profileUrl field is included in vCard`() {
        // Given
        val profile = createTestProfile()
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = false,
            title = false,
            organization = false,
            description = false,
            profileUrl = true,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertNull(organization)
            assertNull(title)
            assertEquals("https://www.gravatar.com/test-hash", profileUrl)
            assertNull(note)
            assertNull(email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only privateEmail preference is enabled then only email field is included in vCard`() {
        // Given
        val privateContactInfo = PrivateContactInfo(
            privateEmail = "private@example.com",
            privatePhone = "987-654-3210"
        )
        val userSharePreferences = UserSharePreferences(
            privateEmail = true,
            privatePhone = false,
            name = false,
            location = false,
            title = false,
            organization = false,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            privateContactInfo = privateContactInfo,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertNull(organization)
            assertNull(title)
            assertNull(profileUrl)
            assertNull(note)
            assertEquals("private@example.com", email)
            assertNull(phoneNumber)
        }
    }

    @Test
    fun `when only privatePhone preference is enabled then only phoneNumber field is included in vCard`() {
        // Given
        val privateContactInfo = PrivateContactInfo(
            privateEmail = "private@example.com",
            privatePhone = "987-654-3210"
        )
        val userSharePreferences = UserSharePreferences(
            privateEmail = false,
            privatePhone = true,
            name = false,
            location = false,
            title = false,
            organization = false,
            description = false,
            profileUrl = false,
            verifiedAccounts = emptyMap()
        )

        // When
        val shareUiState = ShareUiState(
            privateContactInfo = privateContactInfo,
            userSharePreferences = userSharePreferences
        )

        // Then
        with(shareUiState.vCardQrCodeData) {
            assertNull(firstName)
            assertNull(lastName)
            assertNull(nickname)
            assertNull(organization)
            assertNull(title)
            assertNull(profileUrl)
            assertNull(note)
            assertNull(email)
            assertEquals("987-654-3210", phoneNumber)
        }
    }

    private fun createTestProfile() = Profile {
        hash = "test-hash"
        displayName = "Test User"
        profileUrl = URI("https://www.gravatar.com/test-hash")
        avatarUrl = URI("https://www.gravatar.com/avatar/test-hash")
        avatarAltText = "Avatar for Test User"
        description = "Test description"
        pronouns = "They/Them"
        pronunciation = "Test pronunciation"
        location = "Test Location"
        jobTitle = "Software Engineer"
        company = "Test Company"
        firstName = "Test"
        lastName = "User"
        verifiedAccounts = emptyList()
        contactInfo = ProfileContactInfo {
            cellPhone = "123-456-7890"
            email = "test@example.com"
        }
    }
}
