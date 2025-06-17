package com.gravatar.analytics.di

import android.content.Context
import com.gravatar.analytics.tracks.TracksTracker
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class AnalyticsModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkAllModules() {
        analyticsModule.verify(
            injections = injectedParameters(
                definition<TracksTracker>(Context::class)
            )
        )
    }
}
