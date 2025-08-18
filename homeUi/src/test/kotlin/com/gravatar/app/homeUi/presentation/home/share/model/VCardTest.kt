package com.gravatar.app.homeUi.presentation.home.share.model

import android.graphics.drawable.Drawable
import com.gravatar.app.homeUi.presentation.drawableToBase64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VCardTest {

    private lateinit var mockDrawable: Drawable
    private val testBase64String = "TestBase64EncodedPhotoData"

    @Before
    fun setup() {
        mockDrawable = mockk<Drawable>()
        mockkStatic(::drawableToBase64)
        every { drawableToBase64(mockDrawable, any(), any()) } returns Result.success(testBase64String)
    }

    @After
    fun tearDown() {
        unmockkStatic(::drawableToBase64)
    }

    @Test
    fun `builder creates VCard with all fields correctly`() {
        val vCard = VCard.Builder()
            .firstName("John")
            .lastName("Doe")
            .nickname("Johnny")
            .organization("Gravatar")
            .title("Developer")
            .profileUrl("https://gravatar.com/johndoe")
            .note("Awesome developer")
            .phoneNumber("123-456-7890")
            .email("john.doe@example.com")
            .location("San Jose, CA")
            .verifiedAccounts(
                listOf(
                    VCard.URL("Facebook", "https://facebook.com/johndoe"),
                    VCard.URL("Twitter", "https://twitter.com/johndoe")
                )
            )
            .build()

        val vCardString = vCard.toString()

        assertTrue(vCardString.contains("BEGIN:VCARD"))
        assertTrue(vCardString.contains("VERSION:3.0"))
        assertTrue(vCardString.contains("PRODID:Gravatar Android"))
        assertTrue(vCardString.contains("N:Doe;John;;;"))
        assertTrue(vCardString.contains("FN:"))
        assertTrue(vCardString.contains("NICKNAME:Johnny"))
        assertTrue(vCardString.contains("ORG:Gravatar"))
        assertTrue(vCardString.contains("TITLE:Developer"))
        assertTrue(vCardString.contains("URL:https://gravatar.com/johndoe"))
        assertTrue(vCardString.contains("NOTE:Awesome developer"))
        assertTrue(vCardString.contains("TEL;TYPE=cell:123-456-7890"))
        assertTrue(vCardString.contains("EMAIL:john.doe@example.com"))
        assertTrue(vCardString.contains("ADR;CHARSET=UTF-8;TYPE=HOME:;;;San Jose, CA;;;"))
        assertTrue(
            vCardString.contains(
                "URL;TYPE=\"Facebook\":https://facebook.com/johndoe\n" +
                    "URL;TYPE=\"Twitter\":https://twitter.com/johndoe\n"
            )
        )
        assertTrue(vCardString.endsWith("END:VCARD"))
    }

    @Test
    fun `builder creates VCard with some fields correctly`() {
        val vCard = VCard.Builder()
            .firstName("Jane")
            .organization("Automattic")
            .email("jane.doe@example.com")
            .build()

        val vCardString = vCard.toString()

        assertTrue(vCardString.contains("BEGIN:VCARD"))
        assertTrue(vCardString.contains("VERSION:3.0"))
        assertTrue(vCardString.contains("PRODID:Gravatar Android"))
        assertTrue(vCardString.contains("N:;Jane;;;")) // Last name is empty
        assertTrue(vCardString.contains("FN:"))
        assertTrue(vCardString.contains("ORG:Automattic"))
        assertTrue(vCardString.contains("EMAIL:jane.doe@example.com"))
        assertTrue(vCardString.endsWith("END:VCARD"))

        // Ensure other fields are not present
        assertTrue(!vCardString.contains("TITLE:"))
        assertTrue(!vCardString.contains("URL:"))
        assertTrue(!vCardString.contains("NOTE:"))
        assertTrue(!vCardString.contains("TEL;TYPE=cell:"))
        assertTrue(!vCardString.contains("ADR;CHARSET=UTF-8;TYPE=HOME:"))
        assertTrue(!vCardString.contains("URL;TYPE="))
    }

    @Test
    fun `builder creates empty VCard correctly`() {
        val vCard = VCard.Builder().build()
        val vCardString = vCard.toString()

        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "FN:\n" + // FN is required but empty
                "END:VCARD",
            vCardString
        )
    }

    @Test
    fun `builder handles null inputs gracefully`() {
        val vCard = VCard.Builder()
            .firstName(null)
            .lastName(null)
            .nickname(null)
            .organization(null)
            .title(null)
            .profileUrl(null)
            .note(null)
            .phoneNumber(null)
            .email(null)
            .location(null)
            .build()
        val vCardString = vCard.toString()

        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "FN:\n" + // FN is required but empty
                "END:VCARD",
            vCardString
        )
    }

    @Test
    fun `builder handles empty string inputs correctly`() {
        val vCard = VCard.Builder()
            .firstName("")
            .lastName("")
            .nickname("")
            .organization("")
            .title("")
            .profileUrl("")
            .note("")
            .phoneNumber("")
            .email("")
            .location("")
            .build()
        val vCardString = vCard.toString()
        // Empty strings should behave like nulls and not result in fields like "ORG:"
        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "FN:\n" + // FN is required but empty
                "END:VCARD",
            vCardString
        )
    }

    @Test
    fun `builder replaces newlines with spaces in field values`() {
        val vCard = VCard.Builder()
            .firstName("First\nName")
            .lastName("Last\nName")
            .nickname("Nick\nName")
            .organization("Org\nName")
            .title("Job\nTitle")
            .profileUrl("http://example.com/profile\nurl")
            .note("This is a\nnote with\nnewlines.")
            .phoneNumber("123\n456\n7890")
            .email("user\nname@example.com")
            .location("San\nJose")
            .build()

        val vCardString = vCard.toString()

        assertTrue(vCardString.contains("N:Last Name;First Name;;;"))
        assertTrue(vCardString.contains("FN:"))
        assertTrue(vCardString.contains("NICKNAME:Nick Name"))
        assertTrue(vCardString.contains("ORG:Org Name"))
        assertTrue(vCardString.contains("TITLE:Job Title"))
        assertTrue(vCardString.contains("URL:http://example.com/profile url"))
        assertTrue(vCardString.contains("NOTE:This is a note with newlines."))
        assertTrue(vCardString.contains("TEL;TYPE=cell:123 456 7890"))
        assertTrue(vCardString.contains("EMAIL:user name@example.com"))
        assertTrue(vCardString.contains("ADR;CHARSET=UTF-8;TYPE=HOME:;;;San Jose;;;"))
    }

    @Test
    fun `when no firstName and lastName but nickname exists, N field uses nickname`() {
        val vCard = VCard.Builder()
            .nickname("JohnDoe")
            .organization("Gravatar")
            .email("john.doe@example.com")
            .build()

        val vCardString = vCard.toString()

        assertTrue(vCardString.contains("BEGIN:VCARD"))
        assertTrue(vCardString.contains("VERSION:3.0"))
        assertTrue(vCardString.contains("PRODID:Gravatar Android"))
        assertTrue(vCardString.contains("N:;JohnDoe;;;")) // N field should use nickname
        assertTrue(vCardString.contains("NICKNAME:JohnDoe"))
        assertTrue(vCardString.contains("ORG:Gravatar"))
        assertTrue(vCardString.contains("EMAIL:john.doe@example.com"))
        assertTrue(vCardString.endsWith("END:VCARD"))
    }

    @Test
    fun `when photo is available and withPhoto is true, photo is included in vCard`() {
        // Given
        val vCard = VCard.Builder()
            .firstName("John")
            .lastName("Doe")
            .photo(mockDrawable)
            .build()

        // When
        val vCardString = vCard.exportToString(withPhoto = true)

        // Then
        assertTrue(vCardString.contains("PHOTO;ENCODING=b;TYPE=JPEG:$testBase64String"))
    }

    @Test
    fun `when photo is available and withPhoto is false, photo is not included in vCard`() {
        // Given
        val vCard = VCard.Builder()
            .firstName("John")
            .lastName("Doe")
            .photo(mockDrawable)
            .build()

        // When
        val vCardString = vCard.exportToString(withPhoto = false)

        // Then
        assertFalse(vCardString.contains("PHOTO;ENCODING=b;TYPE=JPEG:"))
    }

    @Test
    fun `when photo is null and withPhoto is true, photo is not included in vCard`() {
        // Given
        val vCard = VCard.Builder()
            .firstName("John")
            .lastName("Doe")
            .photo(null)
            .build()

        // When
        val vCardString = vCard.exportToString(withPhoto = true)

        // Then
        assertFalse(vCardString.contains("PHOTO;ENCODING=b;TYPE=JPEG:"))
    }
}
