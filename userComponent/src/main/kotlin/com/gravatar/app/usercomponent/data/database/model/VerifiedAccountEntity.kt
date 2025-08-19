package com.gravatar.app.usercomponent.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.gravatar.restapi.models.VerifiedAccount
import java.net.URI

@Entity(
    tableName = "verified_accounts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["profile_user_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["profile_user_id"])]
)
data class VerifiedAccountEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "profile_user_id")
    val profileUserId: Int,

    @ColumnInfo(name = "service_type")
    val serviceType: String,

    @ColumnInfo(name = "service_label")
    val serviceLabel: String,

    @ColumnInfo(name = "service_icon")
    val serviceIcon: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean,
)

internal fun VerifiedAccountEntity.toVerifiedAccount(): VerifiedAccount {
    return VerifiedAccount {
        serviceType = this@toVerifiedAccount.serviceType
        serviceLabel = this@toVerifiedAccount.serviceLabel
        serviceIcon = URI(this@toVerifiedAccount.serviceIcon)
        url = URI(this@toVerifiedAccount.url)
        isHidden = this@toVerifiedAccount.isHidden
    }
}

internal fun VerifiedAccount.toEntity(userId: Int): VerifiedAccountEntity {
    return VerifiedAccountEntity(
        profileUserId = userId,
        serviceType = this.serviceType,
        serviceLabel = this.serviceLabel,
        serviceIcon = this.serviceIcon.toString(),
        url = this.url.toString(),
        isHidden = this.isHidden,
    )
}
