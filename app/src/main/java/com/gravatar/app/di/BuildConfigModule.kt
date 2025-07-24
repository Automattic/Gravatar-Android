package com.gravatar.app.di

import com.gravatar.app.BuildConfig
import com.gravatar.app.homeUi.AppVersion
import org.koin.dsl.module

val buildConfigModule = module {
    single { AppVersion(BuildConfig.VERSION_NAME) }
}
