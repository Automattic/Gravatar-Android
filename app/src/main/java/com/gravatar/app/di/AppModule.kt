package com.gravatar.app.di

import com.gravatar.app.home_ui.di.homeUiModule
import com.gravatar.app.login_ui.di.loginUiModule
import org.koin.dsl.module

val appModule = module {
    includes(
        homeUiModule,
        loginUiModule,
    )
}
