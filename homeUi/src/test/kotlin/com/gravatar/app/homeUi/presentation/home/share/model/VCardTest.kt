package com.gravatar.app.homeUi.presentation.home.share.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VCardTest {

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
            .build()

        val vCardString = vCard.toString()

        assertTrue(vCardString.contains("BEGIN:VCARD"))
        assertTrue(vCardString.contains("VERSION:3.0"))
        assertTrue(vCardString.contains("PRODID:Gravatar Android"))
        assertTrue(vCardString.contains("N:Doe;John;;;"))
        assertTrue(vCardString.contains("FN:John Doe"))
        assertTrue(vCardString.contains("NICKNAME:Johnny"))
        assertTrue(vCardString.contains("ORG:Gravatar"))
        assertTrue(vCardString.contains("TITLE:Developer"))
        assertTrue(vCardString.contains("URL:https://gravatar.com/johndoe"))
        assertTrue(vCardString.contains("NOTE:Awesome developer"))
        assertTrue(vCardString.contains("TEL;TYPE=cell:123-456-7890"))
        assertTrue(vCardString.contains("EMAIL:john.doe@example.com"))
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
        assertTrue(vCardString.contains("FN:Jane")) // FN falls back to first name
        assertTrue(vCardString.contains("NICKNAME:Jane")) // Nickname falls back to FN
        assertTrue(vCardString.contains("ORG:Automattic"))
        assertTrue(vCardString.contains("EMAIL:jane.doe@example.com"))
        assertTrue(vCardString.endsWith("END:VCARD"))

        // Ensure other fields are not present
        assertTrue(!vCardString.contains("TITLE:"))
        assertTrue(!vCardString.contains("URL:"))
        assertTrue(!vCardString.contains("NOTE:"))
        assertTrue(!vCardString.contains("TEL;TYPE=cell:"))
    }

    @Test
    fun `builder creates empty VCard correctly`() {
        val vCard = VCard.Builder().build()
        val vCardString = vCard.toString()

        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "END:VCARD",
            vCardString
        )
    }

    @Test
    fun `FN and NICKNAME fall back correctly when names or nickname are empty`() {
        // Case 1: Only first name
        var vCard = VCard.Builder().firstName("Solo").build()
        var vCardString = vCard.toString()
        assertTrue(vCardString.contains("N:;Solo;;;"))
        assertTrue(vCardString.contains("FN:Solo"))
        assertTrue(vCardString.contains("NICKNAME:Solo"))

        // Case 2: Only last name
        vCard = VCard.Builder().lastName("OnlyLastName").build()
        vCardString = vCard.toString()
        assertTrue(vCardString.contains("N:OnlyLastName;;;;"))
        assertTrue(vCardString.contains("FN:OnlyLastName"))
        assertTrue(vCardString.contains("NICKNAME:OnlyLastName"))

        // Case 3: First name, last name, but empty nickname
        vCard = VCard.Builder().firstName("First").lastName("Last").nickname("").build()
        vCardString = vCard.toString()
        assertTrue(vCardString.contains("N:Last;First;;;"))
        assertTrue(vCardString.contains("FN:First Last"))
        assertTrue(vCardString.contains("NICKNAME:First Last")) // Falls back to FN

        // Case 4: Only nickname
        vCard = VCard.Builder().nickname("JustNickname").build()
        vCardString = vCard.toString()
        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "NICKNAME:JustNickname\n" +
                "END:VCARD",
            vCardString
        )

        // Case 5: First name, last name, and nickname (nickname should take precedence for NICKNAME field)
        vCard = VCard.Builder().firstName("Official").lastName("Name").nickname("PreferredNick").build()
        vCardString = vCard.toString()
        assertTrue(vCardString.contains("N:Name;Official;;;"))
        assertTrue(vCardString.contains("FN:Official Name"))
        assertTrue(vCardString.contains("NICKNAME:PreferredNick"))
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
            .build()
        val vCardString = vCard.toString()

        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
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
            .build()
        val vCardString = vCard.toString()
        // Empty strings should behave like nulls and not result in fields like "ORG:"
        assertEquals(
            "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "PRODID:Gravatar Android\n" +
                "END:VCARD",
            vCardString
        )
    }
}
