package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.restapi.models.Profile

internal data class ShareUiState(
    val profile: Profile? = null,
    val avatarUrl: String? = null,
    val isAboutAppDialogVisible: Boolean = false,
    val privateContactInfo: PrivateContactInfo = PrivateContactInfo.Default,
    val userSharePreferences: UserSharePreferences = UserSharePreferences.Default,
    val isPrivateInformationDialogVisible: Boolean = false,
) {
    val privateContactState = PrivateContactState(
        emailValue = privateContactInfo.privateEmail,
        isEmailShared = userSharePreferences.privateEmail,
        phoneValue = privateContactInfo.privatePhone,
        isPhoneShared = userSharePreferences.privatePhone,
    )

    val vCardQrCodeData: String = generateVCardData(profile, privateContactState)

    fun copyWithUserSharePreferences(
        shareFieldType: ShareFieldType,
    ): ShareUiState = this.copy(
        userSharePreferences = userSharePreferences.copy(
            privateEmail = if (shareFieldType is ShareFieldType.PrivateEmail) shareFieldType.checked else userSharePreferences.privateEmail,
            privatePhone = if (shareFieldType is ShareFieldType.PrivatePhone) shareFieldType.checked else userSharePreferences.privatePhone,
            name = if (shareFieldType is ShareFieldType.Name) shareFieldType.checked else userSharePreferences.name,
            location = if (shareFieldType is ShareFieldType.Location) shareFieldType.checked else userSharePreferences.location,
            title = if (shareFieldType is ShareFieldType.Title) shareFieldType.checked else userSharePreferences.title,
            organization = if (shareFieldType is ShareFieldType.Organization) shareFieldType.checked else userSharePreferences.organization,
            description = if (shareFieldType is ShareFieldType.Description) shareFieldType.checked else userSharePreferences.description,
            profileUrl = if (shareFieldType is ShareFieldType.ProfileUrl) shareFieldType.checked else userSharePreferences.profileUrl,
        )
    )
}

internal data class PrivateContactState(
    val emailValue: String = "",
    val isEmailShared: Boolean = true,
    val phoneValue: String = "",
    val isPhoneShared: Boolean = false,
)

internal sealed class ShareFieldType {
    abstract val checked: Boolean

    data class Name(
        override val checked: Boolean
    ) : ShareFieldType()

    data class Location(
        override val checked: Boolean
    ) : ShareFieldType()

    data class Title(
        override val checked: Boolean
    ) : ShareFieldType()

    data class Organization(
        override val checked: Boolean
    ) : ShareFieldType()

    data class Description(
        override val checked: Boolean
    ) : ShareFieldType()

    data class ProfileUrl(
        override val checked: Boolean
    ) : ShareFieldType()

    data class PrivateEmail(
        override val checked: Boolean
    ) : ShareFieldType()

    data class PrivatePhone(
        override val checked: Boolean
    ) : ShareFieldType()
}

private fun generateVCardData(profile: Profile?, privateContactInfo: PrivateContactState): String {
    val vCardBuilder = StringBuilder()
        .append("BEGIN:VCARD\n")
        .append("VERSION:3.0\n")
        .append("PRODID:Gravatar Android\n")

    // Add name information if available
    if (profile != null) {
        val firstName = profile.firstName.orEmpty()
        val lastName = profile.lastName.orEmpty()
        if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            vCardBuilder.append("N:$lastName;$firstName;;;\n")
                .append("FN:${("$firstName $lastName".trim()).ifEmpty { profile.displayName }}\n")
                .append("NICKNAME:${profile.displayName.ifEmpty { "$firstName $lastName".trim() }}\n")
        }

        // Add organization information if available
        if (profile.company.isNotEmpty()) {
            vCardBuilder.append("ORG:${profile.company}\n")
        }

        // Add job title if available
        if (profile.jobTitle.isNotEmpty()) {
            vCardBuilder.append("TITLE:${profile.jobTitle}\n")
        }

        // Add URL
        vCardBuilder.append("URL:${profile.profileUrl}\n")

        // Add Note
        if (profile.description.isNotEmpty()) {
            vCardBuilder.append("NOTE:${profile.description}\n")
        }
    }

    // Add private contact info if shared
    if (privateContactInfo.isPhoneShared && privateContactInfo.phoneValue.isNotEmpty()) {
        vCardBuilder.append("TEL;TYPE=cell:${privateContactInfo.phoneValue}\n")
    }

    if (privateContactInfo.isEmailShared && privateContactInfo.emailValue.isNotEmpty()) {
        vCardBuilder.append("EMAIL:${privateContactInfo.emailValue}\n")
    }

    vCardBuilder.append("END:VCARD")
    return vCardBuilder.toString()
}
