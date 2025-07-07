package com.gravatar.app.usercomponent.di

import com.gravatar.app.usercomponent.data.database.ProfileDao
import com.gravatar.app.usercomponent.data.database.UserDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val databaseModule = module {
    single<UserDatabase> { UserDatabase.getInstance(androidContext()) }
    single<ProfileDao> { get<UserDatabase>().profileDao() }
}