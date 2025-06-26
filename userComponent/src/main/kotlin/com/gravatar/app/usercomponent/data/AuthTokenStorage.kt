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

    suspend fun clear()
}

internal class DatastoreAuthTokenStorage(
    @UserPrefs private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider,
) : AuthTokenStorage {

    companion object {
        private const val KEY = "auth_token"
    }

    private val tokenKey = stringPreferencesKey(KEY)

    @Suppress("SwallowedException")
    override suspend fun get(): String? = withContext(dispatcherProvider.io) {
        try {
            dataStore.data.first()[tokenKey]
        } catch (_: IOException) {
            null
        }
    }

    override suspend fun save(token: String): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    override suspend fun clear(): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }
}
