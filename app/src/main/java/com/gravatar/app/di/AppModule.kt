package com.gravatar.app.di

import com.gravatar.analytics.TrackerSetupDataProvider
import com.gravatar.analytics.di.analyticsModule
import com.gravatar.app.AppTrackerSetupDataProvider
import com.gravatar.app.clock.di.clockModule
import com.gravatar.app.homeUi.di.homeUiModule
import com.gravatar.app.loginUi.di.loginUiModule
import com.gravatar.app.networkmonitor.di.networkMonitorModule
import com.gravatar.crashlogging.di.crashLoggingModule
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    includes(
        homeUiModule,
        loginUiModule,
        analyticsModule,
        dispatcherModule,
        clockModule,
        networkMonitorModule,
        buildConfigModule,
        crashLoggingModule,
    )
    singleOf(::AppTrackerSetupDataProvider) { bind<TrackerSetupDataProvider>() }
}
