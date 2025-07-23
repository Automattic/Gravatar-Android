package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class ShareUiStateTest {

    @Test
    fun `when ShareUiState has complete profile and shared contact info then vCardQrCodeData contains all information`() {
        // Given
        val profile = createCompleteProfile()
        val privateContactInfo = PrivateContactInfo(
            emailValue = "test@example.com",
            isEmailShared = true,
            phoneValue = "123-456-7890",
            isPhoneShared = true
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            avatarUrl = "https://www.gravatar.com/avatar/test-hash",
            privateContactInfo = privateContactInfo
        )

        // Then
        val vCardData = shareUiState.vCardQrCodeData

        // Verify vCard structure
        assertTrue(vCardData.startsWith("BEGIN:VCARD"))
        assertTrue(vCardData.endsWith("END:VCARD"))
        assertTrue(vCardData.contains("VERSION:3.0"))

        // Verify profile information
        assertTrue(vCardData.contains("N:User;Test;;;"))
        assertTrue(vCardData.contains("FN:Test User"))
        assertTrue(vCardData.contains("NICKNAME:Test User"))
        assertTrue(vCardData.contains("ORG:Test Company"))
        assertTrue(vCardData.contains("TITLE:Software Engineer"))
        assertTrue(vCardData.contains("URL:https://www.gravatar.com/test-hash"))
        assertTrue(vCardData.contains("NOTE:Test description"))

        // Verify contact information
        assertTrue(vCardData.contains("TEL;TYPE=cell:123-456-7890"))
        assertTrue(vCardData.contains("EMAIL:test@example.com"))
    }

    @Test
    fun `when ShareUiState has minimal profile data then vCardQrCodeData contains only available information`() {
        // Given
        val profile = createMinimalProfile()

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            avatarUrl = null,
            privateContactInfo = PrivateContactInfo()
        )

        // Then
        var vCardData = shareUiState.vCardQrCodeData

        // Verify vCard structure
        assertTrue(vCardData.startsWith("BEGIN:VCARD"))
        assertTrue(vCardData.endsWith("END:VCARD"))

        // Remove BEGIN:VCARD and VERSION lines for easier verification
        vCardData = vCardData.replace("BEGIN:VCARD\n", "")
            .replace("END:VCARD\n", "")
            .replace("VERSION:3.0\n", "")

        // Verify minimal profile information is included
        assertTrue(vCardData.contains("URL:https://www.gravatar.com/minimal-hash"))

        // Verify that optional fields are not included
        assertFalse("vCard should not contain N:", vCardData.contains("N:"))
        assertFalse("vCard should not contain FN:", vCardData.contains("FN:"))
        assertFalse("vCard should not contain NICKNAME:", vCardData.contains("NICKNAME:"))
        assertFalse("vCard should not contain ORG:", vCardData.contains("ORG:"))
        assertFalse("vCard should not contain TITLE:", vCardData.contains("TITLE:"))
        assertFalse("vCard should not contain NOTE:", vCardData.contains("NOTE:"))
        assertFalse("vCard should not contain TEL;", vCardData.contains("TEL;"))
        assertFalse("vCard should not contain EMAIL:", vCardData.contains("EMAIL:"))
    }

    @Test
    fun `when ShareUiState has unshared contact info then vCardQrCodeData does not include private contact info`() {
        // Given
        val profile = createCompleteProfile()
        val privateContactInfo = PrivateContactInfo(
            emailValue = "test@example.com",
            isEmailShared = false,
            phoneValue = "123-456-7890",
            isPhoneShared = false
        )

        // When
        val shareUiState = ShareUiState(
            profile = profile,
            avatarUrl = "https://www.gravatar.com/avatar/test-hash",
            privateContactInfo = privateContactInfo
        )

        // Then
        val vCardData = shareUiState.vCardQrCodeData

        // Verify profile information is included
        assertTrue(vCardData.contains("N:User;Test;;;"))
        assertTrue(vCardData.contains("FN:Test User"))

        // Verify private contact info is not included
        assertFalse("vCard should not contain TEL;", vCardData.contains("TEL;"))
        assertFalse("vCard should not contain EMAIL:", vCardData.contains("EMAIL:"))
    }

    private fun createCompleteProfile() = Profile {
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

    private fun createMinimalProfile() = Profile {
        hash = "minimal-hash"
        displayName = ""
        profileUrl = URI("https://www.gravatar.com/minimal-hash")
        avatarUrl = URI("https://www.gravatar.com/avatar/minimal-hash")
        avatarAltText = ""
        description = ""
        pronouns = ""
        pronunciation = ""
        location = ""
        jobTitle = ""
        company = ""
        firstName = ""
        lastName = ""
        verifiedAccounts = emptyList()
        contactInfo = ProfileContactInfo {
            cellPhone = ""
            email = ""
        }
    }
}
