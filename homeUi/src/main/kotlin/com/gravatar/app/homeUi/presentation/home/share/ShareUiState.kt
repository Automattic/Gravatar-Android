package com.gravatar.app.homeUi.presentation.home.share

import android.graphics.drawable.Drawable
import com.gravatar.app.homeUi.presentation.home.share.model.VCard
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
    val isQrCodeExpanded: Boolean = false,
    val isPrivacySettingVisible: Boolean = false,
    private val avatarDrawable: Drawable? = null,
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
            verifiedAccounts = if (shareFieldType is ShareFieldType.VerifiedAccount) {
                userSharePreferences.verifiedAccounts.toMutableMap().apply {
                    this[shareFieldType.url] = shareFieldType.checked
                }
            } else {
                userSharePreferences.verifiedAccounts
            }
        )
    )

    val vCardQrCodeData: VCard = VCard.Builder()
        .firstName(profile?.firstName.takeIf { userSharePreferences.name })
        .lastName(profile?.lastName.takeIf { userSharePreferences.name })
        .nickname(profile?.displayName.takeIf { userSharePreferences.description })
        .organization(profile?.company.takeIf { userSharePreferences.organization })
        .title(profile?.jobTitle.takeIf { userSharePreferences.title })
        .profileUrl(profile?.profileUrl.toString().takeIf { userSharePreferences.profileUrl })
        .note(profile?.description.takeIf { userSharePreferences.description })
        .phoneNumber(privateContactState.phoneValue.takeIf { privateContactState.isPhoneShared })
        .email(privateContactState.emailValue.takeIf { privateContactState.isEmailShared })
        .location(profile?.location.takeIf { userSharePreferences.location })
        .photo(avatarDrawable)
        .verifiedAccounts(
            profile?.verifiedAccounts.orEmpty()
                .filter { userSharePreferences.verifiedAccountUrlChecked(it.url.toString()) }
                .map {
                    VCard.URL(
                        label = it.serviceLabel,
                        url = it.url.toString()
                    )
                }
        )
        .build()
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

    data class VerifiedAccount(
        val url: String,
        override val checked: Boolean
    ) : ShareFieldType()
}
