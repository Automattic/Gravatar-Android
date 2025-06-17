package com.gravatar.app

import android.app.Application
import com.gravatar.analytics.di.analyticsModule
import com.gravatar.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GravatarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GravatarApplication)
            modules(appModule, analyticsModule)
        }
    }
}
