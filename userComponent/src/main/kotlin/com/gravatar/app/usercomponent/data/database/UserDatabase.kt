package com.gravatar.app.usercomponent.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import com.gravatar.app.usercomponent.data.database.model.VerifiedAccountEntity

@Database(
    entities = [ProfileEntity::class, VerifiedAccountEntity::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ],
)
internal abstract class UserDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object Companion {
        private const val DATABASE_NAME = "user-database"

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
