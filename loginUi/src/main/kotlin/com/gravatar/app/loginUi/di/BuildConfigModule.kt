package com.gravatar.app.loginUi.di

import com.gravatar.app.loginUi.BuildConfig
import com.gravatar.app.loginUi.presentation.oauth.OAuthConfig
import org.koin.dsl.module

internal val buildConfigModule = module {
    single {
        OAuthConfig(
            clientId = BuildConfig.OAUTH_CLIENT_ID,
            redirectUri = BuildConfig.OAUTH_REDIRECT_URI,
            clientSecret = BuildConfig.OAUTH_CLIENT_SECRET,
        )
    }
}
