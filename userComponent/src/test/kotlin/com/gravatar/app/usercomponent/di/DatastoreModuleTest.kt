package com.gravatar.app.usercomponent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.gravatar.app.foundations.DispatcherProvider
import com.gravatar.app.usercomponent.data.DatastoreAuthTokenStorage
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class DatastoreModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkDatastoreModule() {
        datastoreModule.verify(
            injections = injectedParameters(
                definition<DataStore<Preferences>>(Context::class),
                definition<DatastoreAuthTokenStorage>(DispatcherProvider::class)
            )
        )
    }
}
