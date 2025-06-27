package com.gravatar.app.usercomponent.di

import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class DatastoreModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkDatastoreModule() {
        datastoreModule.verify()
    }
}
