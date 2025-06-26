package com.gravatar.app.usercomponent.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.di.UserPrefs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal interface AuthTokenStorage {
    suspend fun get(): String?

    suspend fun save(token: String)

    suspend fun clearToken()
}

internal class DatastoreAuthTokenStorage(
    @UserPrefs private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider,
) : AuthTokenStorage {

    companion object {
        private const val KEY = "auth_token"
    }

    @Suppress("SwallowedException")
    override suspend fun get(): String? = withContext(dispatcherProvider.io) {
        try {
            dataStore.data.first()[stringPreferencesKey(KEY)]
        } catch (_: IOException) {
            null
        }
    }

    override suspend fun save(token: String): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(KEY)] = token
        }
    }

    override suspend fun clearToken(): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(KEY))
        }
    }
}
