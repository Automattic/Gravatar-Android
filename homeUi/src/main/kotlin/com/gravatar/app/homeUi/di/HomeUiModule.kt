package com.gravatar.app.homeUi.di

import com.gravatar.app.homeUi.ImageDownloader
import com.gravatar.app.homeUi.presentation.DrawableUtils
import com.gravatar.app.homeUi.presentation.FileUtils
import com.gravatar.app.homeUi.presentation.home.HomeViewModel
import com.gravatar.app.homeUi.presentation.home.components.topbar.TopBarPickerPopupViewModel
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.AboutAppDialogViewModel
import com.gravatar.app.homeUi.presentation.home.gravatar.GravatarViewModel
import com.gravatar.app.homeUi.presentation.home.profile.ProfileViewModel
import com.gravatar.app.homeUi.presentation.home.share.ShareViewModel
import com.gravatar.app.usercomponent.di.userComponentModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeUiModule = module {
    factory<ImageDownloader> { ImageDownloader(androidContext()) }
    factoryOf(::FileUtils)
    factoryOf(::DrawableUtils)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::GravatarViewModel)
    viewModelOf(::TopBarPickerPopupViewModel)
    viewModelOf(::AboutAppDialogViewModel)
    viewModelOf(::ShareViewModel)
    viewModelOf(::HomeViewModel)

    includes(userComponentModule)
}
