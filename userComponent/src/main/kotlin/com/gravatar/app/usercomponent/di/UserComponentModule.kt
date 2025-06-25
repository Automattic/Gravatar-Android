package com.gravatar.app.usercomponent.di

import com.gravatar.app.usercomponent.data.RealAuthRepository
import com.gravatar.app.usercomponent.data.WordPressClient
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val userComponentModule = module {
    factoryOf(::RealAuthRepository) { bind<AuthRepository>() }
    factoryOf(::WordPressClient)
    includes(httpClientModule)
}
