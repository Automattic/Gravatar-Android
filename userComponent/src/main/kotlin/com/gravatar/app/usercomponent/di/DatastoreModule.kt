package com.gravatar.app.usercomponent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gravatar.app.usercomponent.data.AuthTokenStorage
import com.gravatar.app.usercomponent.data.AvatarCacheBusterStorage
import com.gravatar.app.usercomponent.data.DatastoreUserPrefsStorage
import com.gravatar.app.usercomponent.data.PrivacySettingsStorage
import com.gravatar.app.usercomponent.data.PrivateContactInfoStorage
import com.gravatar.app.usercomponent.data.UserSharePreferencesStorage
import com.gravatar.app.usercomponent.data.UserStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.Named
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val datastoreModule = module {
    single<DataStore<Preferences>>(qualifier = named<UserPrefs>()) {
        androidContext().userPreferencesDataStore
    }
    factory<AuthTokenStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
    factory<AvatarCacheBusterStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
    factory<UserStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
    factory<UserSharePreferencesStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
    factory<PrivateContactInfoStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
    factory<PrivacySettingsStorage> {
        DatastoreUserPrefsStorage(
            dataStore = get(qualifier = named<UserPrefs>()),
            dispatcherProvider = get()
        )
    }
}

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user-preferences")

@Named
internal annotation class UserPrefs
