package com.gravatar.app.usercomponent.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.di.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal interface AuthTokenStorage {
    suspend fun getToken(): String?

    suspend fun saveToken(token: String)

    suspend fun clearToken()
}

internal interface AvatarCacheBusterStorage {

    fun getAvatarCacheBuster(): Flow<String?>

    suspend fun saveAvatarCacheBuster(value: String)
}

/**
 * Convenient interface to clear all user related data in one call.
 */
internal interface UserStorage : AuthTokenStorage, AvatarCacheBusterStorage {
    suspend fun clear()
}

internal class DatastoreUserPrefsStorage(
    @UserPrefs private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider,
) : UserStorage {

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val AVATAR_CACHE_BUSTER_KEY = "avatar_cache_buster"
    }

    private val tokenKey = stringPreferencesKey(AUTH_TOKEN_KEY)
    private val avatarCacheBusterKey = stringPreferencesKey(AVATAR_CACHE_BUSTER_KEY)

    override suspend fun getToken(): String? {
        return try {
            dataStore.data
                .map { preferences ->
                    preferences[tokenKey]
                }
                .flowOn(dispatcherProvider.io)
                .firstOrNull()
        } catch (_: IOException) {
            null
        }
    }

    override suspend fun saveToken(token: String): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    override suspend fun clearToken(): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
        }
    }

    override fun getAvatarCacheBuster(): Flow<String?> {
        return dataStore.data
            .map { preferences ->
                preferences[avatarCacheBusterKey]
            }
            .catch { emit(null) }
            .flowOn(dispatcherProvider.io)
    }

    override suspend fun saveAvatarCacheBuster(value: String): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[avatarCacheBusterKey] = value
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
