package com.gravatar.app.clock.di

import com.gravatar.app.clock.AppClock
import com.gravatar.app.clock.SystemAppClock
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val clockModule = module {
    factoryOf(::SystemAppClock) { bind<AppClock>() }
}
