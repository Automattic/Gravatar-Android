package com.gravatar.app.usercomponent.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.di.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal interface AuthTokenStorage {
    fun get(): Flow<String?>

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

    override fun get(): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[tokenKey]
            }
            .catch { emit(null) }
            .flowOn(dispatcherProvider.io)
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
