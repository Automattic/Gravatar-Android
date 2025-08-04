package com.gravatar.app.di

import com.gravatar.app.restapi.GravatarApi
import com.gravatar.app.services.GravatarService
import com.gravatar.app.services.GravatarServiceImpl
import com.squareup.moshi.Moshi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val apiModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.gravatar.com/v3/")
            .addConverterFactory(
                MoshiConverterFactory.create()
            )
            .build()
    }
    single<GravatarApi> { get<Retrofit>().create(GravatarApi::class.java) }
    single<GravatarService> {
        GravatarServiceImpl(
            gravatarApi = get(),
            dispatchers = get()
        )
    }
}