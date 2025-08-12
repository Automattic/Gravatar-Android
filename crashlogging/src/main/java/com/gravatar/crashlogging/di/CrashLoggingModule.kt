package com.gravatar.crashlogging.di

import com.automattic.android.tracks.crashlogging.CrashLoggingDataProvider
import com.automattic.android.tracks.crashlogging.CrashLoggingProvider
import com.gravatar.crashlogging.ContextBasedLocaleProvider
import com.gravatar.crashlogging.GravatarCrashLoggingDataProvider
import com.gravatar.crashlogging.LocaleProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val crashLoggingModule = module {
    factory<LocaleProvider> {
        ContextBasedLocaleProvider(
            context = androidApplication()
        )
    }
    factoryOf(::GravatarCrashLoggingDataProvider) {
        bind<CrashLoggingDataProvider>()
    }
    single {
        CrashLoggingProvider.createInstance(
            context = get(),
            dataProvider = get(),
            appScope = get()
        )
    }
}
