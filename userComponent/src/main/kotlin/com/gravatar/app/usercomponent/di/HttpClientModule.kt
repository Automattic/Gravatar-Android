package com.gravatar.app.usercomponent.di

import com.gravatar.app.di.apiModule
import com.gravatar.app.usercomponent.data.interceptors.UnauthorizeInterceptor
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.services.AvatarService
import com.gravatar.services.ProfileService
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.dsl.module

internal val httpClientModule = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<HttpClient> {
        HttpClient(get()) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    single { UnauthorizeInterceptor(applicationScope = get<CoroutineScope>(), logout = lazy { get<Logout>() }) }
    single {
        OkHttpClient.Builder()
            .addInterceptor(interceptor = get<UnauthorizeInterceptor>())
            .build()
    }
    single { ProfileService(okHttpClient = get<OkHttpClient>()) }
    single { AvatarService(okHttpClient = get<OkHttpClient>()) }

    includes(apiModule)
}
