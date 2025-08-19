package com.gravatar.app.usercomponent.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.ProfileContactInfo
import com.gravatar.restapi.models.VerifiedAccount
import java.net.URI

@Entity(tableName = "user_profiles")
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "profile_url")
    val profileUrl: String,
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,
    @ColumnInfo(name = "avatar_alt_text")
    val avatarAltText: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "pronouns")
    val pronouns: String,
    @ColumnInfo(name = "pronunciation")
    val pronunciation: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "job_title")
    val jobTitle: String,
    @ColumnInfo(name = "company")
    val company: String,
    @ColumnInfo(name = "first_name")
    val firstName: String? = null,
    @ColumnInfo(name = "last_name")
    val lastName: String? = null,
    @ColumnInfo(name = "contact_cell_phone")
    val contactCellPhone: String? = null,
    @ColumnInfo(name = "contact_email")
    val contactEmail: String? = null,
) {
    /**
     * Converts this entity to a Profile model.
     */
    fun toProfile(verifiedAccounts: List<VerifiedAccount> = emptyList()): Profile {
        return Profile {
            userId = this@ProfileEntity.userId
            hash = this@ProfileEntity.hash
            displayName = this@ProfileEntity.displayName
            profileUrl = URI(this@ProfileEntity.profileUrl)
            avatarUrl = URI(this@ProfileEntity.avatarUrl)
            avatarAltText = this@ProfileEntity.avatarAltText
            description = this@ProfileEntity.description
            pronouns = this@ProfileEntity.pronouns
            pronunciation = this@ProfileEntity.pronunciation
            location = this@ProfileEntity.location
            jobTitle = this@ProfileEntity.jobTitle
            company = this@ProfileEntity.company
            firstName = this@ProfileEntity.firstName
            lastName = this@ProfileEntity.lastName
            contactInfo = ProfileContactInfo {
                cellPhone = this@ProfileEntity.contactCellPhone
                email = this@ProfileEntity.contactEmail
            }
            this.verifiedAccounts = verifiedAccounts
        }
    }

    companion object {
        /**
         * Creates a ProfileEntity from a Profile model.
         */
        fun fromProfile(profile: Profile): ProfileEntity {
            return ProfileEntity(
                userId = profile.userId!!,
                hash = profile.hash,
                displayName = profile.displayName,
                profileUrl = profile.profileUrl.toString(),
                avatarUrl = profile.avatarUrl.toString(),
                avatarAltText = profile.avatarAltText,
                description = profile.description,
                pronouns = profile.pronouns,
                pronunciation = profile.pronunciation,
                location = profile.location,
                jobTitle = profile.jobTitle,
                company = profile.company,
                firstName = profile.firstName,
                lastName = profile.lastName,
                contactCellPhone = profile.contactInfo?.cellPhone,
                contactEmail = profile.contactInfo?.email,
            )
        }
    }
}
