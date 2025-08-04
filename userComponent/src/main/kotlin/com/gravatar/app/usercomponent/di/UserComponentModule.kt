package com.gravatar.app.usercomponent.di

import com.gravatar.app.usercomponent.data.InMemoryUserSessionPersistence
import com.gravatar.app.usercomponent.data.RealAuthRepository
import com.gravatar.app.usercomponent.data.RealProfileRepository
import com.gravatar.app.usercomponent.data.RealUserRepository
import com.gravatar.app.usercomponent.data.UserSessionPersistence
import com.gravatar.app.usercomponent.data.WordPressClient
import com.gravatar.app.usercomponent.domain.facade.PrivateContactInfoFacade
import com.gravatar.app.usercomponent.domain.facade.PrivateContactInfoOperations
import com.gravatar.app.usercomponent.domain.facade.UserSharePreferencesFacade
import com.gravatar.app.usercomponent.domain.facade.UserSharePreferencesOperations
import com.gravatar.app.usercomponent.domain.repository.AuthRepository
import com.gravatar.app.usercomponent.domain.repository.ProfileRepository
import com.gravatar.app.usercomponent.domain.repository.UserRepository
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserAvatar
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserAvatarUseCase
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfile
import com.gravatar.app.usercomponent.domain.usecase.DeleteUserProfileUseCase
import com.gravatar.app.usercomponent.domain.usecase.FetchAvatarsUseCase
import com.gravatar.app.usercomponent.domain.usecase.FetchUserAvatars
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrl
import com.gravatar.app.usercomponent.domain.usecase.GetAvatarUrlUseCase
import com.gravatar.app.usercomponent.domain.usecase.GetPrivateContactInfo
import com.gravatar.app.usercomponent.domain.usecase.GetPrivateContactInfoUseCase
import com.gravatar.app.usercomponent.domain.usecase.GetUserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.GetUserSharePreferencesUseCase
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedIn
import com.gravatar.app.usercomponent.domain.usecase.IsUserLoggedInUseCase
import com.gravatar.app.usercomponent.domain.usecase.Login
import com.gravatar.app.usercomponent.domain.usecase.LoginUseCase
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.app.usercomponent.domain.usecase.LogoutUseCase
import com.gravatar.app.usercomponent.domain.usecase.SelectAvatarUseCase
import com.gravatar.app.usercomponent.domain.usecase.SelectUserAvatar
import com.gravatar.app.usercomponent.domain.usecase.UpdatePrivateContactInfo
import com.gravatar.app.usercomponent.domain.usecase.UpdatePrivateContactInfoUseCase
import com.gravatar.app.usercomponent.domain.usecase.UpdateUserSharePreferences
import com.gravatar.app.usercomponent.domain.usecase.UpdateUserSharePreferencesUseCase
import com.gravatar.app.usercomponent.domain.usecase.UploadAvatarUseCase
import com.gravatar.app.usercomponent.domain.usecase.UploadUserAvatar
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
    factoryOf(::GetAvatarUrlUseCase) { bind<GetAvatarUrl>() }
    factoryOf(::SelectAvatarUseCase) { bind<SelectUserAvatar>() }
    factoryOf(::DeleteUserAvatarUseCase) { bind<DeleteUserAvatar>() }
    factoryOf(::FetchAvatarsUseCase) { bind<FetchUserAvatars>() }
    factoryOf(::UploadAvatarUseCase) { bind<UploadUserAvatar>() }
    factoryOf(::GetUserSharePreferencesUseCase) { bind<GetUserSharePreferences>() }
    factoryOf(::UpdateUserSharePreferencesUseCase) { bind<UpdateUserSharePreferences>() }
    factoryOf(::GetPrivateContactInfoUseCase) { bind<GetPrivateContactInfo>() }
    factoryOf(::UpdatePrivateContactInfoUseCase) { bind<UpdatePrivateContactInfo>() }
    factoryOf(::DeleteUserProfileUseCase) { bind<DeleteUserProfile>() }
    factoryOf(::UserSharePreferencesOperations) { bind<UserSharePreferencesFacade>() }
    factoryOf(::PrivateContactInfoOperations) { bind<PrivateContactInfoFacade>() }
    factoryOf(::WordPressClient)
    singleOf(::InMemoryUserSessionPersistence) { bind<UserSessionPersistence>() }
    includes(httpClientModule)
    includes(datastoreModule)
    includes(databaseModule)
}
