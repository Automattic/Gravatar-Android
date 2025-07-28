package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.app.usercomponent.domain.model.PrivateContactInfo
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.restapi.models.Profile

internal data class ShareUiState(
    val profile: Profile? = null,
    val avatarUrl: String? = null,
    val isAboutAppDialogVisible: Boolean = false,
    val privateContactInfo: PrivateContactInfo = PrivateContactInfo.Default,
    val userSharePreferences: UserSharePreferences = UserSharePreferences.Default
) {

    val privateContactState = PrivateContactState(
        emailValue = privateContactInfo.privateEmail,
        isEmailShared = userSharePreferences.privateEmail,
        phoneValue = privateContactInfo.privatePhone,
        isPhoneShared = userSharePreferences.privatePhone,
    )

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
    val isEmailShared: Boolean = false,
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
