package com.gravatar.app.usercomponent.di

import com.gravatar.app.usercomponent.data.InMemoryUserSessionPersistence
import com.gravatar.app.usercomponent.data.RealAuthRepository
import com.gravatar.app.usercomponent.data.RealProfileRepository
import com.gravatar.app.usercomponent.data.RealUserRepository
import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.data.WordPressClient
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedIn
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedInUseCase
import com.gravatar.app.usercomponent.domain.usecase.Login
import com.gravatar.app.usercomponent.domain.usecase.LoginUseCase
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.app.usercomponent.domain.usecase.LogoutUseCase
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val userComponentModule = module {
    factoryOf(::RealProfileRepository) { bind<ProfileRepository>() }
    factoryOf(::RealAuthRepository) { bind<AuthRepository>() }
    factoryOf(::RealUserRepository) { bind<UserRepository>() }
    factoryOf(::LoginUseCase) { bind<Login>() }
    factoryOf(::LogoutUseCase) { bind<Logout>() }
    factoryOf(::IsUserLoggedInUseCase) { bind<IsUserLoggedIn>() }
    factoryOf(::WordPressClient)
    singleOf(::InMemoryUserSessionPersistence) { bind<UserSessionPersistence>() }
    single { ProfileService() }
    single { AvatarService() }
    includes(httpClientModule)
    includes(datastoreModule)
    includes(databaseModule)
}
