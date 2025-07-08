package com.gravatar.app.di

import com.gravatar.analytics.di.analyticsModule
import com.gravatar.app.clock.di.clockModule
import com.gravatar.app.homeUi.di.homeUiModule
import com.gravatar.app.loginUi.di.loginUiModule
import org.koin.dsl.module

val appModule = module {
    includes(
        homeUiModule,
        loginUiModule,
        analyticsModule,
        dispatcherModule,
        clockModule,
    )
}
