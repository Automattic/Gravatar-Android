package com.gravatar.analytics.di

import com.automattic.android.tracks.TracksClient
import com.gravatar.analytics.Tracker
import com.gravatar.analytics.tracks.TracksTracker
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val analyticsModule = module {
    single { TracksClient.getClient(get()) }
    singleOf(::TracksTracker) { bind<Tracker>() }
}
