package com.gravatar.app.homeUi.presentation.home.share

import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import com.gravatar.restapi.models.Profile

internal data class ShareUiState(
    val profile: Profile? = null,
    val avatarUrl: String? = null,
    val isAboutAppDialogVisible: Boolean = false,
    val privateContactInfo: PrivateContactInfo = PrivateContactInfo(),
    val userSharePreferences: UserSharePreferences = UserSharePreferences(
        name = true,
        location = true,
        title = true,
        organization = true,
        description = true,
        profileUrl = true
    )
) {
    fun copyWithUserSharePreferences(
        shareFieldType: ShareFieldType,
    ): ShareUiState = this.copy(
        userSharePreferences = userSharePreferences.copy(
            name = if (shareFieldType is ShareFieldType.Name) shareFieldType.checked else userSharePreferences.name,
            location = if (shareFieldType is ShareFieldType.Location) shareFieldType.checked else userSharePreferences.location,
            title = if (shareFieldType is ShareFieldType.Title) shareFieldType.checked else userSharePreferences.title,
            organization = if (shareFieldType is ShareFieldType.Organization) shareFieldType.checked else userSharePreferences.organization,
            description = if (shareFieldType is ShareFieldType.Description) shareFieldType.checked else userSharePreferences.description,
            profileUrl = if (shareFieldType is ShareFieldType.ProfileUrl) shareFieldType.checked else userSharePreferences.profileUrl,
        )
    )
}

internal data class PrivateContactInfo(
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
}
