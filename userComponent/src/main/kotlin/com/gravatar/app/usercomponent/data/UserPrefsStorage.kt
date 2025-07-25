package com.gravatar.app.usercomponent.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.di.UserPrefs
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
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

internal interface UserSharePreferencesStorage {
    fun getUserSharePreferences(): Flow<UserSharePreferences>

    suspend fun saveUserSharePreferences(userSharePreferences: UserSharePreferences)
}

/**
 * Convenient interface to clear all user related data in one call.
 */
internal interface UserStorage : AuthTokenStorage, AvatarCacheBusterStorage, UserSharePreferencesStorage {
    suspend fun clear()
}

internal class DatastoreUserPrefsStorage(
    @UserPrefs private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider,
) : UserStorage {

    companion object {
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val AVATAR_CACHE_BUSTER_KEY = "avatar_cache_buster"
        private const val USER_SHARE_NAME_KEY = "share_name"
        private const val USER_SHARE_LOCATION_KEY = "share_location"
        private const val USER_SHARE_TITLE_KEY = "share_title"
        private const val USER_SHARE_ORGANIZATION_KEY = "share_organization"
        private const val USER_SHARE_DESCRIPTION_KEY = "share_description"
        private const val USER_SHARE_PROFILE_URL_KEY = "share_profile_url"
    }

    private val tokenKey = stringPreferencesKey(AUTH_TOKEN_KEY)
    private val avatarCacheBusterKey = stringPreferencesKey(AVATAR_CACHE_BUSTER_KEY)
    private val userShareNameKey = booleanPreferencesKey(USER_SHARE_NAME_KEY)
    private val userShareLocationKey = booleanPreferencesKey(USER_SHARE_LOCATION_KEY)
    private val userShareTitleKey = booleanPreferencesKey(USER_SHARE_TITLE_KEY)
    private val userShareOrganizationKey = booleanPreferencesKey(USER_SHARE_ORGANIZATION_KEY)
    private val userShareDescriptionKey = booleanPreferencesKey(USER_SHARE_DESCRIPTION_KEY)
    private val userShareProfileUrlKey = booleanPreferencesKey(USER_SHARE_PROFILE_URL_KEY)

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

    override fun getUserSharePreferences(): Flow<UserSharePreferences> {
        return dataStore.data
            .map { preferences ->
                UserSharePreferences(
                    name = preferences[userShareNameKey] ?: true,
                    location = preferences[userShareLocationKey] ?: true,
                    title = preferences[userShareTitleKey] ?: true,
                    organization = preferences[userShareOrganizationKey] ?: true,
                    description = preferences[userShareDescriptionKey] ?: true,
                    profileUrl = preferences[userShareProfileUrlKey] ?: true
                )
            }
            .catch { emit(UserSharePreferences.Default) }
            .flowOn(dispatcherProvider.io)
    }

    override suspend fun saveUserSharePreferences(
        userSharePreferences: UserSharePreferences
    ): Unit = withContext(dispatcherProvider.io) {
        dataStore.edit { preferences ->
            preferences[userShareNameKey] = userSharePreferences.name
            preferences[userShareLocationKey] = userSharePreferences.location
            preferences[userShareTitleKey] = userSharePreferences.title
            preferences[userShareOrganizationKey] = userSharePreferences.organization
            preferences[userShareDescriptionKey] = userSharePreferences.description
            preferences[userShareProfileUrlKey] = userSharePreferences.profileUrl
        }
    }
}
