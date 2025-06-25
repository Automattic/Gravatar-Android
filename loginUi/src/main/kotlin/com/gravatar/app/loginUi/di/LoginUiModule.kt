package com.gravatar.app.loginUi.di

import com.gravatar.app.foundations.di.dispatcherModule
import com.gravatar.app.loginUi.presentation.login.LoginViewModel
import com.gravatar.app.usercomponent.di.userComponentModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val loginUiModule = module {
    includes(buildConfigModule, userComponentModule, dispatcherModule)

    viewModelOf(::LoginViewModel)
}
