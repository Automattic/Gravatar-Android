package com.gravatar.app.usercomponent.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileWithVerifiedAccounts(
    @Embedded val profile: ProfileEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "profile_user_id"
    )
    val verifiedAccounts: List<VerifiedAccountEntity>
)
