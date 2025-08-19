package com.gravatar.app.usercomponent.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.testUtils.CoroutineTestRule
import com.gravatar.app.usercomponent.domain.model.UserSharePreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DatastoreUserPrefsStorageTest {

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>

    private val dispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
    }

    private val tmpDir: File = kotlin.io.path.createTempDirectory().toFile()

    private fun createDataStore(): DataStore<Preferences> {
        // Create a DataStore with a unique file per test class instance
        return PreferenceDataStoreFactory.create(
            produceFile = { File(tmpDir, "user_prefs_test.preferences_pb") }
        )
    }

    private fun initStorage(): DatastoreUserPrefsStorage {
        if (!::dataStore.isInitialized) {
            dataStore = createDataStore()
        }
        return DatastoreUserPrefsStorage(
            dataStore = dataStore,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @After
    fun tearDown() {
        // Clean up the DataStore file
        try {
            File(tmpDir, "user_prefs_test.preferences_pb").deleteRecursively()
        } catch (_: Exception) {
        }
    }

    @Test
    fun `saveUserSharePreferences then getUserSharePreferences should roundtrip including verified accounts`() = runTest {
        val storage = initStorage()

        val prefsToSave = UserSharePreferences.Default.copy(
            privateEmail = false,
            privatePhone = false,
            name = false,
            location = true,
            title = false,
            organization = true,
            description = false,
            profileUrl = true,
            verifiedAccounts = mapOf(
                "twitter" to true,
                "github" to false,
                "mastodon" to true,
            )
        )

        storage.saveUserSharePreferences(prefsToSave)

        storage.getUserSharePreferences().test {
            assertEquals(prefsToSave, awaitItem())
        }
    }

    @Test
    fun `getUserSharePreferences should return empty verified accounts when malformed string stored`() = runTest {
        val storage = initStorage()

        // Manually write a malformed verified accounts string
        val key = stringPreferencesKey("verified_accounts")
        dataStore.edit { prefs ->
            prefs[key] = "twitter-true;github-false;" // missing '=' should be considered malformed and result in emptyMap
        }

        storage.getUserSharePreferences().test {
            val loaded = awaitItem()
            assertEquals(UserSharePreferences.Default.copy(verifiedAccounts = emptyMap()), loaded)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
