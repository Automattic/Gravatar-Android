package com.gravatar.app.usercomponent.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getProfile(): ProfileEntity?

    @Query("DELETE FROM user_profiles")
    suspend fun delete()
}
