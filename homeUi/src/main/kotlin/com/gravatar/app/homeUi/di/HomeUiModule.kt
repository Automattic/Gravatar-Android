package com.gravatar.app.homeUi.di

import com.gravatar.app.homeUi.presentation.home.gravatar.GravatarViewModel
import com.gravatar.app.homeUi.presentation.home.profile.ProfileViewModel
import com.gravatar.services.ProfileService
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeUiModule = module {
    single { ProfileService() }
    viewModelOf(::ProfileViewModel)
    viewModelOf(::GravatarViewModel)
}
