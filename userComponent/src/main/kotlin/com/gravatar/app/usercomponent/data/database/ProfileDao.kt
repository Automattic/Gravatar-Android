package com.gravatar.app.usercomponent.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import com.gravatar.app.usercomponent.data.database.model.ProfileWithVerifiedAccounts
import com.gravatar.app.usercomponent.data.database.model.VerifiedAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerifiedAccounts(accounts: List<VerifiedAccountEntity>)

    @Query("DELETE FROM verified_accounts WHERE profile_user_id = :userId")
    suspend fun deleteVerifiedAccountsForUser(userId: Int)

    @Transaction
    suspend fun insertProfileWithVerifiedAccounts(profile: ProfileEntity, accounts: List<VerifiedAccountEntity>) {
        insertProfile(profile)
        deleteVerifiedAccountsForUser(profile.userId)
        if (accounts.isNotEmpty()) insertVerifiedAccounts(accounts)
    }

    @Transaction
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getProfileWithVerifiedAccounts(): Flow<ProfileWithVerifiedAccounts?>

    @Query("DELETE FROM user_profiles")
    suspend fun delete()
}
