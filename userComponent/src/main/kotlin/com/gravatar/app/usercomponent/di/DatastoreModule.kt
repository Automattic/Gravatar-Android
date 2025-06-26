package com.gravatar.app.usercomponent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gravatar.app.usercomponent.data.AuthTokenStorage
import com.gravatar.app.usercomponent.data.DatastoreAuthTokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.Named
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val datastoreModule = module {
    single<DataStore<Preferences>>(qualifier = named<UserPrefs>()) {
        androidContext().userPreferencesDataStore
    }
    factory<AuthTokenStorage> {
        DatastoreAuthTokenStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
}

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user-preferences")

@Named
internal annotation class UserPrefs
