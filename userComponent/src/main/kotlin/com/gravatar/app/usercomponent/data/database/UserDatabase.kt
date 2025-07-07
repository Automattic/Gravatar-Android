package com.gravatar.app.usercomponent.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = true)
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
